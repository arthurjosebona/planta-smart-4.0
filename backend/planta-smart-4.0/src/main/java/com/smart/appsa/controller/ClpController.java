package com.smart.appsa.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
import com.smart.appsa.model.enums.Estacao;
import com.smart.appsa.service.clp.ClpReadingService;
import com.smart.appsa.service.clp.PlcDataStore;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/smart")
@AllArgsConstructor
public class ClpController {

    private final ClpReadingService readingManager;
    private final PlcDataStore dataStore;
    private final AppStateConfig appStateConfig;
    private final ClpIpConfig clpIpConfig;

    @PostMapping("/start-readings")
    public ResponseEntity<String> startReadings(@RequestBody Map<String, String> ips) {
        readingManager.start(ips);
        return ResponseEntity.ok("Leituras iniciadas.");
    }

    @PostMapping("/stop-readings")
    public ResponseEntity<String> stopReadings() {
        readingManager.stop();
        return ResponseEntity.ok("Leituras interrompidas e eventos registrados.");
    }

    @GetMapping("/data/{clp}")
    public ResponseEntity<String> getData(@PathVariable String clp) {
        Optional<Estacao> estacao = estacaoDoApelido(clp);
        if (estacao.isEmpty()) {
            return ResponseEntity.ok("CLP inválido: " + clp);
        }

        String hex = dataStore.getHex(estacao.get());
        if (hex == null) {
            return ResponseEntity.ok("Ainda não há dados para " + clp);
        }
        return ResponseEntity.ok(hex);
    }

    @GetMapping("/smartstream/{bancada}")
    public SseEmitter smartStream(@PathVariable String bancada) {
        SseEmitter emitter = new SseEmitter(0L);
        ExecutorService sseExecutor = Executors.newSingleThreadExecutor();

        sseExecutor.execute(() -> {
            try {
                while (true) {
                    byte[] dados = dadosDoStream(bancada);

                    if (dados != null) {
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("leitura")
                                    .data(PlcDataStore.toHex(dados)));
                        } catch (IOException | IllegalStateException ex) {
                            emitter.complete();
                            break;
                        }
                    }

                    TimeUnit.MILLISECONDS.sleep(400);
                }
            } catch (InterruptedException e) {
                emitter.completeWithError(e);
                Thread.currentThread().interrupt();
            }
        });

        emitter.onCompletion(() -> System.out.println("SSE finalizado para " + bancada));
        emitter.onTimeout(emitter::complete);
        emitter.onError(emitter::completeWithError);

        return emitter;
    }

    private byte[] dadosDoStream(String bancada) {
        if (Estacao.ESTOQUE.getNome().equalsIgnoreCase(bancada)) {
            return dataStore.getEstoqueComStatus();
        }
        return Estacao.fromNome(bancada)
                .map(dataStore::getRaw)
                .orElse(null);
    }

    /** Mapeia os apelidos legados clp1..clp4 (ou o nome da estação) para a estação. */
    private Optional<Estacao> estacaoDoApelido(String clp) {
        return switch (clp.toLowerCase()) {
            case "clp1" -> Optional.of(Estacao.ESTOQUE);
            case "clp2" -> Optional.of(Estacao.PROCESSO);
            case "clp3" -> Optional.of(Estacao.MONTAGEM);
            case "clp4" -> Optional.of(Estacao.EXPEDICAO);
            default -> Estacao.fromNome(clp);
        };
    }

    @PostMapping("/smart/ping")
    public Map<String, Boolean> pingHosts() {
        Map<String, Boolean> resultados = new HashMap<>();

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
            resultados.put(nome, isClpOnline);
        });

        return resultados;
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
    public boolean getReadOnly() {
        return appStateConfig.isReadOnly();
    }
}
