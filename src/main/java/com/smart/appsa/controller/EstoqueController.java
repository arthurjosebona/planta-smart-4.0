package com.smart.appsa.controller;

import com.smart.appsa.model.Estoque;
import com.smart.appsa.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping("/disponivel")
    public ResponseEntity<List<Estoque>> listarDisponivel() {
        return ResponseEntity.ok(estoqueService.listarDisponivel());
    }
}