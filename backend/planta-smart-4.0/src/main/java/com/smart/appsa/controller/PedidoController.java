package com.smart.appsa.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.service.PedidoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PedidoController {
    private final PedidoService pedidoService;

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

    @PutMapping("/{id}/status")
    public ResponseEntity<PedidoResponseDTO> updateStatusToConcluido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.updateStatusAsCompleted(id));
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

}
