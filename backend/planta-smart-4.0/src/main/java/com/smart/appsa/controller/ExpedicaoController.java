package com.smart.appsa.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smart.appsa.dto.request.ExpedicaoRequestDTO;
import com.smart.appsa.dto.response.ExpedicaoResponseDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.service.ExpedicaoService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/expedicao")
@RequiredArgsConstructor
public class ExpedicaoController {
    private final ExpedicaoService service;

    @GetMapping("")
    public ResponseEntity<List<ExpedicaoResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ExpedicaoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("")
    public ResponseEntity<Void> updateAll(@RequestBody List<ExpedicaoRequestDTO> entity) {
        service.updateAll(entity);
        return ResponseEntity.noContent().build();
    }    
}
