package com.smart.appsa.controller;


import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.ProducaoListService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;
    private final ProducaoListService producaoListService;


    @PostMapping("")
    public ResponseEntity<PedidoResponseDTO> create(@RequestBody PedidoRequestDTO entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.create(entity));
    }


    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.findById(id));
    }


    @GetMapping("")
    public ResponseEntity<List<PedidoResponseDTO>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @PutMapping("/start-production/{id}")
    public ResponseEntity<Void> startProduction(@PathVariable Long id) {
        producaoListService.addOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/production-queue") 
    public ResponseEntity<List<Long>> getQueue() {
        return ResponseEntity.ok(producaoListService.filaAtual());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pedidoService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }


    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> update(
            @PathVariable Long id,
            @RequestBody PedidoRequestDTO requestDTO) {
        return ResponseEntity.ok(pedidoService.update(id, requestDTO));
    }

    @GetMapping("/op/{op}")
    public ResponseEntity<PedidoResponseDTO> findPedidoByOp(@PathVariable Integer op) {
        return ResponseEntity.ok(pedidoService.findByOp(op));
    }
    
    @GetMapping("/expedicao/{id}")
    public ResponseEntity<List<PedidoResponseDTO>> findPedidosByExpedicao(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.findPedidosByExpedicao(id));
    }

}
