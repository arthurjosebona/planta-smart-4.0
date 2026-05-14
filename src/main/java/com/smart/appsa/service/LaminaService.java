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

    
    public Lamina adicionarLamina(Lamina lamina) {
        if (lamina.getCor() == null) {
            throw new IllegalArgumentException("Cor da lâmina não pode ser nula");
        }
        if (lamina.getPadrao() == null) {
            throw new IllegalArgumentException("Padrão da lâmina não pode ser nulo");
        }
        if (lamina.getPosicao() == null) {
            throw new IllegalArgumentException("Posição da lâmina não pode ser nula");
        }
        if (lamina.getBloco() == null) {
            throw new IllegalArgumentException("Bloco não pode ser nulo");
        }
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