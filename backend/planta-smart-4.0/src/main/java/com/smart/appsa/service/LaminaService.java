package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.exception.LaminaPosicaoOcupadaException;
import com.smart.appsa.exception.RequiredFieldException;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.repository.LaminaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaminaService {
    private final LaminaRepository laminaRepository;

    @Transactional(readOnly = true)
    public List<Lamina> findAll() {
        return laminaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Lamina findById(Long id) {
        return laminaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lamina", id));
    }

    @Transactional(readOnly = true)
    public List<Lamina> findByBlocoId(Long blocoId) {
        return laminaRepository.findByBlocoId(blocoId);
    }
    
    @Transactional
    public Lamina create(Lamina lamina) {
        validateRequiredFields(lamina);
        return laminaRepository.save(lamina);
    }

    private void validateRequiredFields(Lamina lamina) {
        if (lamina.getCor() == null) 
            throw new RequiredFieldException("CorLamina");
        if (lamina.getPadrao() == null) 
            throw new RequiredFieldException("PadraoLamina");
        if (lamina.getPosicao() == null) 
            throw new RequiredFieldException("PosicaoLamina");
        if (lamina.getBloco() == null) 
            throw new RequiredFieldException("Bloco");
    }

    @Transactional
    public Lamina update(Long id, Lamina laminaAtualizada) {
        Lamina laminaExistente = laminaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Lamina", id));
        validatePosicaoAvailability(laminaExistente, laminaAtualizada);
        updateLaminaFields(laminaExistente, laminaAtualizada);
        return laminaRepository.save(laminaExistente);
    }

    private void validatePosicaoAvailability(Lamina laminaExistente, Lamina laminaNova) {
        List<Lamina> laminasDoBloco = laminaRepository.findByBloco(laminaExistente.getBloco());

        boolean posicaoOcupada = laminasDoBloco.stream()
                .anyMatch(l -> !l.getId().equals(laminaExistente.getId()) 
                    && l.getPosicao() == laminaNova.getPosicao());

        if (posicaoOcupada) throw new LaminaPosicaoOcupadaException(laminaNova.getPosicao());
    }

    private void updateLaminaFields(Lamina laminaExistente, Lamina laminaAtualizada) {
        laminaExistente.setCor(laminaAtualizada.getCor());
        laminaExistente.setPadrao(laminaAtualizada.getPadrao());
        laminaExistente.setPosicao(laminaAtualizada.getPosicao());
    }

    @Transactional
    public void delete(Long id) {
        if (!laminaRepository.existsById(id)) 
            throw new ResourceNotFoundException("Lamina", id);
        laminaRepository.deleteById(id);
    }
}