package com.smart.appsa.controller;

import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.service.PedidoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private PedidoService pedidoService;

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
    public ResponseEntity<Void> atualizarStatusParaConcluido(@PathVariable Long id) {
        pedidoService.atualizarStatusParaConcluido(id);
        return ResponseEntity.noContent().build();
    }
    
}
