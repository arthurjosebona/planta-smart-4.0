package com.smart.appsa.service;

import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.repository.EstoqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    // ─── READ ────────────────────────────────────────────────────────────────────

    public List<Estoque> listarTodos() {
        return estoqueRepository.findAll();
    }

    public Estoque buscarPorId(Long id) {
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado com ID: " + id));
    }

    public List<Estoque> listarDisponivel() {
        return estoqueRepository.findByCorEstoque(CorEstoque.VAZIO);
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    public Estoque adicionarBloco(Estoque request) {
        validarPosicao(request.getPosicaoFisica());

        Estoque posicaoAtual = estoqueRepository.findByPosicaoFisica(request.getPosicaoFisica())
                .orElseThrow(() -> new RuntimeException(
                        "Posição " + request.getPosicaoFisica() + " não encontrada no estoque."));

        if (posicaoAtual.getCorEstoque() != CorEstoque.VAZIO) {
            throw new RuntimeException(
                    "Posição " + request.getPosicaoFisica() + " já está ocupada com bloco de cor "
                            + posicaoAtual.getCorEstoque() + ".");
        }

        posicaoAtual.setCorEstoque(request.getCorEstoque());
        return estoqueRepository.save(posicaoAtual);
    }

    // ─── QUERY: CONTAGEM POR COR ───────────────────────────────────────────────

    public long countByCorEstoque(CorEstoque cor) {
        return estoqueRepository.countByCorEstoque(cor);
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    public Estoque atualizarBloco(Long id, CorEstoque novaCor) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado com ID: " + id));

        if (estoque.getCorEstoque() == CorEstoque.VAZIO) {
            throw new RuntimeException(
                    "Posição " + estoque.getPosicaoFisica() + " está vazia. "
                            + "Use adicionarBloco para inserir um bloco.");
        }

        estoque.setCorEstoque(novaCor);
        return estoqueRepository.save(estoque);
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────────

    public Estoque removerBloco(int posicaoFisica) {
        validarPosicao(posicaoFisica);

        Estoque posicaoAtual = estoqueRepository.findByPosicaoFisica(posicaoFisica)
                .orElseThrow(() -> new RuntimeException(
                        "Posição " + posicaoFisica + " não encontrada no estoque."));

        if (posicaoAtual.getCorEstoque() == CorEstoque.VAZIO) {
            throw new RuntimeException("Posição " + posicaoFisica + " já está vazia.");
        }

        posicaoAtual.setCorEstoque(CorEstoque.VAZIO);
        return estoqueRepository.save(posicaoAtual);
    }

    // ─── HELPER ──────────────────────────────────────────────────────────────────

    private void validarPosicao(int posicao) {
        if (posicao < 1 || posicao > 28) {
            throw new RuntimeException(
                    "Posição inválida: " + posicao + ". O estoque tem 28 posições (1 a 28).");
        }
    }
}