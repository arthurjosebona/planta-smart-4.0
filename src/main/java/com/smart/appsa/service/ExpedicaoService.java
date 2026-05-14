package com.smart.appsa.service;

import com.smart.appsa.model.Expedicao;
import com.smart.appsa.repository.ExpedicaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpedicaoService {

    private final ExpedicaoRepository expedicaoRepository;

    // ─── READ ────────────────────────────────────────────────────────────────────

    public List<Expedicao> listarTodas() {
        return expedicaoRepository.findAll();
    }

    public Expedicao buscarPorId(Long id) {
        return expedicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expedição não encontrada com ID: " + id));
    }

    public Expedicao buscarPorOrdemDeProducao(int ordemDeProducao) {
        return expedicaoRepository.findByOrdemDeProducaoAtual(ordemDeProducao)
                .orElseThrow(() -> new RuntimeException(
                        "Nenhuma expedição encontrada para a ordem: " + ordemDeProducao));
    }

    public int buscarPrimeiraPosicaoLivre() {
        return encontrarProximaPosicaoLivre();
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    public Expedicao registrarExpedicao(int ordemDeProducao) {
        if (expedicaoRepository.existsByOrdemDeProducaoAtual(ordemDeProducao)) {
            throw new RuntimeException(
                    "Ordem de produção " + ordemDeProducao + " já foi expedida.");
        }

        int proximaPosicao = encontrarProximaPosicaoLivre();

        Expedicao expedicao = Expedicao.builder()
                .posicaoFisica(proximaPosicao)
                .ordemDeProducaoAtual(ordemDeProducao)
                .build();

        return expedicaoRepository.save(expedicao);
    }

    public Expedicao atribuirOrdemDeProducao(int posicaoFisica, int ordemDeProducao) {
        if (posicaoFisica < 1 || posicaoFisica > 12) {
            throw new RuntimeException(
                    "Posição inválida: " + posicaoFisica + ". A expedição tem 12 posições (1 a 12).");
        }

        if (expedicaoRepository.existsByOrdemDeProducaoAtual(ordemDeProducao)) {
            throw new RuntimeException(
                    "Ordem de produção " + ordemDeProducao + " já foi expedida.");
        }

        boolean posicaoOcupada = expedicaoRepository.findPosicoesOcupadas().contains(posicaoFisica);
        if (posicaoOcupada) {
            throw new RuntimeException("Posição " + posicaoFisica + " já está ocupada na expedição.");
        }

        Expedicao expedicao = Expedicao.builder()
                .posicaoFisica(posicaoFisica)
                .ordemDeProducaoAtual(ordemDeProducao)
                .build();

        return expedicaoRepository.save(expedicao);
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    public Expedicao atualizarPosicao(Long id, int novaPosicao) {
        if (novaPosicao < 1 || novaPosicao > 12) {
            throw new RuntimeException(
                    "Posição inválida: " + novaPosicao + ". A expedição tem 12 posições (1 a 12).");
        }

        Expedicao expedicao = expedicaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expedição não encontrada com ID: " + id));

        boolean posicaoOcupada = expedicaoRepository.findPosicoesOcupadas().contains(novaPosicao);
        if (posicaoOcupada) {
            throw new RuntimeException("Posição " + novaPosicao + " já está ocupada na expedição.");
        }

        expedicao.setPosicaoFisica(novaPosicao);
        return expedicaoRepository.save(expedicao);
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────────

    public void liberarPosicao(Long id) {
        if (!expedicaoRepository.existsById(id)) {
            throw new RuntimeException("Expedição não encontrada com ID: " + id);
        }
        expedicaoRepository.deleteById(id);
    }

    // ─── HELPER ──────────────────────────────────────────────────────────────────

    private int encontrarProximaPosicaoLivre() {
        List<Integer> posicoesOcupadas = expedicaoRepository.findPosicoesOcupadas();
        for (int pos = 1; pos <= 12; pos++) {
            if (!posicoesOcupadas.contains(pos)) return pos;
        }
        throw new RuntimeException("Expedição lotada. Todas as 12 posições estão ocupadas.");
    }
}