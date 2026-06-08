package com.smart.appsa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.request.EstoqueRequestDTO;
import com.smart.appsa.dto.response.EstoqueResponseDTO;
import com.smart.appsa.service.EstoqueService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping("")
    public ResponseEntity<List<EstoqueResponseDTO>> findAll() {
        return ResponseEntity.ok(estoqueService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.findById(id));
    }

    @GetMapping("/disponivel")
    public ResponseEntity<List<EstoqueResponseDTO>> findAvailable() {
        return ResponseEntity.ok(estoqueService.findAvailable());
    }

    @GetMapping("/indisponivel")
    public ResponseEntity<List<EstoqueResponseDTO>> findUnavailable() {
        return ResponseEntity.ok(estoqueService.findUnavailable());
    }

    @PutMapping("")
    public ResponseEntity<Void> updateAllEstoque(@RequestBody List<EstoqueRequestDTO> estoque) {
        estoqueService.updateAllEstoque(estoque);
        return ResponseEntity.noContent().build();
    }
}