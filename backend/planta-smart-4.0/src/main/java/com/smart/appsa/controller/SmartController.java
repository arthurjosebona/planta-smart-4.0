package com.smart.appsa.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.config.ClpIpConfig;
import com.smart.appsa.dto.clp.ClpReadOnlyDTO;
import com.smart.appsa.dto.clp.ClpStatusPingDTO;
import com.smart.appsa.model.enums.Estacao;
import com.smart.appsa.service.clp.ClpReadingService;
import com.smart.appsa.service.clp.PlcDataStore;
import com.smart.appsa.service.clp.SseService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/smart")
@AllArgsConstructor
public class SmartController {

    private final ClpReadingService clpReadingService;
    private final PlcDataStore dataStore;
    private final AppStateConfig appStateConfig;
    private final ClpIpConfig clpIpConfig;
    private final SseService sseService;

    @PostMapping("/start-readings")
    public ResponseEntity<String> startReadings(@RequestBody Map<String, String> ips) {
        clpReadingService.start(ips);
        return ResponseEntity.ok("Leituras iniciadas.");
    }

    @PostMapping("/stop-readings")
    public ResponseEntity<String> stopReadings() {
        clpReadingService.stop();
        return ResponseEntity.ok("Leituras interrompidas e eventos registrados.");
    }

    @GetMapping("/data/{clp}")
    public ResponseEntity<String> getData(@PathVariable String clp) {
        Optional<Estacao> estacao = Estacao.fromNome(clp);
        if (estacao.isEmpty()) {
            return ResponseEntity.ok("CLP inválido: " + clp);
        }

        String hex = dataStore.getHex(estacao.get());
        if (hex == null) {
            return ResponseEntity.ok("Ainda não há dados para " + clp);
        }
        return ResponseEntity.ok(hex);
    }

    // Stream multiplexado: uma única conexão SSE recebe eventos de todas as estações.
    // Cada evento é nomeado pelo nome da estação
    // e só é emitido quando algum campo do DTO daquela estação muda.
    @GetMapping("/stream")
    public SseEmitter stream() {
        return sseService.subscribe(EnumSet.allOf(Estacao.class));
    }

    // Stream de uma única estação 
    @GetMapping("/stream/{bancada}")
    public SseEmitter stream(@PathVariable String bancada) {
        Estacao estacao = Estacao.fromNome(bancada)
                .orElseThrow(() -> new IllegalArgumentException("CLP inválido: " + bancada));
        return sseService.subscribe(EnumSet.of(estacao));
    }

    @PostMapping("/smart/ping")
    public ResponseEntity<List<ClpStatusPingDTO>> pingHosts() {
        List<ClpStatusPingDTO> statusClps = new ArrayList<>();

        clpIpConfig.getIps().forEach((nome, ip) -> {
            boolean isClpOnline = false;

            try (Socket socket = new Socket()) {
                SocketAddress address = new InetSocketAddress(ip, 102);
                socket.connect(address, 2000); // timeout 2 segundos
                isClpOnline = true;
            } catch (IOException e) {
                isClpOnline = false;
            }

            System.out.println(nome + ": " + isClpOnline);
            statusClps.add(ClpStatusPingDTO.builder().nome(nome).ip(ip).online(isClpOnline).verifiedAt(LocalDateTime.now().toString()).build());
        });

        return ResponseEntity.ok(statusClps);
    }

    @PostMapping("/smart/reset-status")
    public ResponseEntity<String> resetarStatus() {
        appStateConfig.resetarStatus();
        return ResponseEntity.ok("Status zerados com sucesso.");
    }

    @PostMapping("/smart/readonly")
    public ResponseEntity<String> setReadOnly(@RequestParam boolean value) {
        appStateConfig.setReadOnly(value);
        return ResponseEntity.ok("Modo readOnly: " + value);
    }

    @GetMapping("/smart/readonly")
    public ResponseEntity<ClpReadOnlyDTO> getReadOnly() {
        return ResponseEntity.ok(ClpReadOnlyDTO.builder().readOnly(appStateConfig.isReadOnly()).build());
    }
}
