package com.smart.appsa.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.exception.DuplicatePosicaoException;
import com.smart.appsa.exception.LaminasSizeException;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.PosicaoLamina;
import com.smart.appsa.repository.BlocoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlocoService {
    private final BlocoRepository blocoRepository;
    private final LaminaService laminaService;
    private final EstoqueService estoqueService;

    @Transactional(readOnly = true)
    public List<Bloco> findAll() {
        return blocoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Bloco findById(Long id) {
        return blocoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bloco", id));
    }
    

    @Transactional
    public Bloco create(Bloco bloco) {
        validateRequiredFields(bloco);
        validateBusinessRules(bloco);
        Bloco blocoSalvo = createBloco(bloco);
        createLaminas(bloco.getLaminas(), blocoSalvo);
        return blocoSalvo;
    }


    private void validateRequiredFields(Bloco bloco) {
        // if (bloco.getLaminas() == null || bloco.getLaminas().isEmpty()) 
        //     throw new RequiredFieldException("laminas");
    }

    private void validateBusinessRules(Bloco bloco) {
        if (bloco.getLaminas().size() > 3 || bloco.getLaminas().size() < 0) 
            throw new LaminasSizeException(bloco.getLaminas().size());
        validateLaminasPosition(bloco.getLaminas());
    }

    private void validateLaminasPosition(List<Lamina> laminas) {
        Map<PosicaoLamina, Long> countByPosicaoLamina = laminas.stream()
            .collect(Collectors.groupingBy(
                Lamina::getPosicao,
                Collectors.counting()
            ));

        List<PosicaoLamina> duplicatePosicao = countByPosicaoLamina.entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .toList();

        if (!duplicatePosicao.isEmpty()) {
            throw new DuplicatePosicaoException(duplicatePosicao);
        }
    } 

    private Bloco createBloco(Bloco bloco) {
        Estoque estoque = estoqueService.findEntityById(bloco.getEstoque().getId());
        bloco.setEstoque(estoque);
        return blocoRepository.save(bloco);
    }


    private void createLaminas(List<Lamina> laminas, Bloco bloco) {
        for (Lamina lamina : laminas) {
            lamina.setBloco(bloco);
            laminaService.create(lamina);
        }
    }

    public Bloco update(Long id, Bloco newBlock) {
        Bloco existentBlock = blocoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloco", id));
        updateBlocoFields(existentBlock, newBlock);
        return blocoRepository.save(existentBlock);
    }

    private void updateBlocoFields(Bloco existentBlock, Bloco newBlock) {
        existentBlock.setCor(newBlock.getCor());
        existentBlock.setEstoque(newBlock.getEstoque());
        existentBlock.setAndar(newBlock.getAndar());
    }

    public void delete(Long id) {
        if (!blocoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bloco", id);
        }
        blocoRepository.deleteById(id);
    }

    @Transactional
public void deleteAllByPedido(Pedido pedido) {
    blocoRepository.deleteAllByPedido(pedido);
}
}