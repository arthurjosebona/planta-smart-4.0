package com.smart.appsa.service;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.LaminaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LaminaService {

    private final LaminaRepository laminaRepository;
    private final BlocoRepository blocoRepository;

    // ─── READ ────────────────────────────────────────────────────────────────────

    public List<Lamina> listarTodas() {
        return laminaRepository.findAll();
    }

    public Lamina buscarPorId(Long id) {
        return laminaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lâmina não encontrada com ID: " + id));
    }

    public List<Lamina> listarPorBloco(Long blocoId) {
        Bloco bloco = blocoRepository.findById(blocoId)
                .orElseThrow(() -> new RuntimeException("Bloco não encontrado com ID: " + blocoId));
        return laminaRepository.findByBloco(bloco);
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    
    public Lamina adicionarLamina(Long blocoId, Lamina lamina) {
        Bloco bloco = blocoRepository.findById(blocoId)
                .orElseThrow(() -> new RuntimeException("Bloco não encontrado com ID: " + blocoId));

        List<Lamina> laminasExistentes = laminaRepository.findByBloco(bloco);

        if (laminasExistentes.size() >= 3) {
            throw new RuntimeException("Bloco já possui o máximo de 3 lâminas.");
        }

        boolean posicaoOcupada = laminasExistentes.stream()
                .anyMatch(l -> l.getPosicao() == lamina.getPosicao());

        if (posicaoOcupada) {
            throw new RuntimeException(
                    "Já existe uma lâmina na posição " + lamina.getPosicao() + " neste bloco.");
        }

        lamina.setBloco(bloco);
        return laminaRepository.save(lamina);
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    public Lamina atualizarLamina(Long id, Lamina dadosAtualizados) {
        Lamina laminaExistente = laminaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lâmina não encontrada com ID: " + id));

        List<Lamina> laminasDoBloco = laminaRepository.findByBloco(laminaExistente.getBloco());

        boolean posicaoOcupada = laminasDoBloco.stream()
                .anyMatch(l -> !l.getId().equals(id) && l.getPosicao() == dadosAtualizados.getPosicao());

        if (posicaoOcupada) {
            throw new RuntimeException(
                    "Já existe uma lâmina na posição " + dadosAtualizados.getPosicao() + " neste bloco.");
        }

        laminaExistente.setCor(dadosAtualizados.getCor());
        laminaExistente.setPadrao(dadosAtualizados.getPadrao());
        laminaExistente.setPosicao(dadosAtualizados.getPosicao());

        return laminaRepository.save(laminaExistente);
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────────

    public void removerLamina(Long id) {
        if (!laminaRepository.existsById(id)) {
            throw new RuntimeException("Lâmina não encontrada com ID: " + id);
        }
        laminaRepository.deleteById(id);
    }
}