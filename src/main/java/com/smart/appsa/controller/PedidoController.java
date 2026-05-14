package com.smart.appsa.controller;

import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.service.PedidoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private PedidoService pedidoService;

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.findById(id));
    }
    
    @GetMapping("")
    public ResponseEntity<List<PedidoResponseDTO>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @PutMapping("/{id}/status")
    public String atualizarStatusParaConcluido(@PathVariable Long id) {
        return pedidoService.atualizarStatusParaConcluido(id);
    }
    
}
