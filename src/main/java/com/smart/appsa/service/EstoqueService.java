package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.dto.response.EstoqueResponseDTO;
import com.smart.appsa.exception.ResourceNotFoundException;
import com.smart.appsa.mapper.EstoqueMapper;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.repository.EstoqueRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> findAll() {
        return estoqueRepository.findAll().stream().map(e -> EstoqueMapper.mapDTO(e)).toList();
    }

    @Transactional(readOnly = true)
    public EstoqueResponseDTO findById(Long id) {
        return EstoqueMapper.mapDTO(estoqueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Estoque", id)));
    }

    @Transactional(readOnly = true)
    public Estoque findEntityById(Long id) {
        return estoqueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Estoque", id));
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> findAvailable() {
        return estoqueRepository.findByCorEstoqueNot(CorEstoque.VAZIO).stream().map(e -> EstoqueMapper.mapDTO(e)).toList(); 
    }

    @Transactional(readOnly = true)
    public List<EstoqueResponseDTO> findUnavailable() {
        return estoqueRepository.findByCorEstoque(CorEstoque.VAZIO).stream().map(e -> EstoqueMapper.mapDTO(e)).toList(); 
    }
    
    @Transactional(readOnly = true)
    public Long countByCorEstoque(CorEstoque cor) {
        return estoqueRepository.countByCorEstoque(cor);
    }

    @Transactional
    public Estoque assignBlockColor(Long idEstoque, CorEstoque novaCor) {
        Estoque estoque = estoqueRepository.findById(idEstoque)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque", idEstoque));

        estoque.setCorEstoque(novaCor);
        return estoqueRepository.save(estoque);
    }
}