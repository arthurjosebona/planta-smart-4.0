package com.smart.appsa.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.repository.BlocoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlocoService {

    private final BlocoRepository blocoRepository;
    private final LaminaService laminaService;

    // ─── READ ────────────────────────────────────────────────────────────────────

    public List<Bloco> findAll() {
        return blocoRepository.findAll();
    }

    public Bloco findById(Long id) {
        return blocoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bloco não encontrado com ID: " + id));
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    public Bloco create(Bloco bloco) {
        // 3. Valida máximo de lâminas se já vieram lâminas no objeto
        if (bloco.getLaminas() == null || bloco.getLaminas().isEmpty()) {
            throw new RuntimeException("O bloco deve conter pelo menos uma lâmina");
        }

        List<Lamina> laminas = new ArrayList<>();

        for (Lamina lamina : bloco.getLaminas()) {
            lamina.setBloco(bloco);
            laminas.add(laminaService.adicionarLamina(lamina));
        }

        validarMaximoLaminas(bloco);

        return blocoRepository.save(bloco);
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    public Bloco atualizarBloco(Long id, Bloco dadosAtualizados) {
        Bloco blocoExistente = blocoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bloco não encontrado com ID: " + id));

        

        blocoExistente.setCor(dadosAtualizados.getCor());
        blocoExistente.setEstoque(dadosAtualizados.getEstoque());

        return blocoRepository.save(blocoExistente);
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────────

    public void removerBloco(Long id) {
        if (!blocoRepository.existsById(id)) {
            throw new RuntimeException("Bloco não encontrado com ID: " + id);
        }
        blocoRepository.deleteById(id);
    }

    // ─── VALIDAÇÕES ──────────────────────────────────────────────────────────────


    /**
     * Valida que o bloco não ultrapasse 3 lâminas.
     * Chamado pelo LaminaService antes de adicionar uma lâmina.
     */
    public void validarMaximoLaminas(Bloco bloco) {
        List<Lamina> laminas = bloco.getLaminas();
        if (laminas != null && laminas.size() >= 3 && laminas.size() < 0) {
            throw new RuntimeException(
                    "Bloco ID " + bloco.getId() + " já possui o máximo de 3 lâminas.");
        }
    }

}