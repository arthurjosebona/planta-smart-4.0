package com.smart.appsa.controller;

import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping("")
    public ResponseEntity<List<Estoque>> listarTodos() {
        return ResponseEntity.ok(estoqueService.listarTodos());
    }

    @GetMapping("/disponivel")
    public ResponseEntity<List<Estoque>> listarDisponivel() {
        return ResponseEntity.ok(estoqueService.listarDisponivel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estoque> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.buscarPorId(id));
    }

    @PostMapping("")
    public ResponseEntity<Estoque> adicionarBloco(@RequestBody Estoque request) {
        return ResponseEntity.ok(estoqueService.adicionarBloco(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Estoque> atualizarBloco(@PathVariable Long id, @RequestBody CorEstoque novaCor) {
        return ResponseEntity.ok(estoqueService.atualizarBloco(id, novaCor));
    }

    @DeleteMapping("/{posicaoFisica}")
    public ResponseEntity<Estoque> removerBloco(@PathVariable int posicaoFisica) {
        return ResponseEntity.ok(estoqueService.removerBloco(posicaoFisica));
    }
}