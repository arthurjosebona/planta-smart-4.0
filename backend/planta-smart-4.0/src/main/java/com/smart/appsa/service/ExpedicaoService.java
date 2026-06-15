package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.dto.request.ExpedicaoRequestDTO;
import com.smart.appsa.dto.response.ExpedicaoResponseDTO;
import com.smart.appsa.exception.ExpedicaoLotadaException;
import com.smart.appsa.exception.InvalidPosicaoExpedicaoException;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.mapper.ExpedicaoMapper;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.repository.ExpedicaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpedicaoService {
    private final ExpedicaoRepository expedicaoRepository;

    @Transactional(readOnly = true)
    public List<ExpedicaoResponseDTO> findAll() {
        return expedicaoRepository.findAll()
            .stream()
            .map(e -> ExpedicaoMapper.mapDto(e))
            .toList();
    }

    @Transactional(readOnly = true)
    public ExpedicaoResponseDTO findById(Long id) {
        return ExpedicaoMapper.mapDto(
            expedicaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expedicao", id))
        );
    }

    @Transactional(readOnly = true)
    public Expedicao findByOrdemDeProducao(int ordemDeProducao) {
        return expedicaoRepository.findByOrdemDeProducaoAtual(ordemDeProducao)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Expedicao", 
                    "Ordem de produção atual", 
                    ordemDeProducao
                ));
    }

    @Transactional
    public void assignOrdemAtPosicao(int ordemDeProducao, int posicaoFisica) {
        validateFields(ordemDeProducao, posicaoFisica);
        Expedicao expedicao = expedicaoRepository.findByPosicaoFisica(posicaoFisica)
                .orElseThrow(() -> new RuntimeException("Posição não encontrada no banco."));
        expedicao.setOrdemDeProducaoAtual(ordemDeProducao);
        expedicaoRepository.save(expedicao);
    }

    private void validateFields(int ordemDeProducao, int posicaoFisica) {
        if (posicaoFisica < 1 || posicaoFisica > 12) 
            throw new InvalidPosicaoExpedicaoException(posicaoFisica);
        // if (expedicaoRepository.findPosicoesOcupadas().contains(posicaoFisica)) 
        //     throw new PosicaoExpedicaoOcupadaException(posicaoFisica);
        // if (expedicaoRepository.existsByOrdemDeProducaoAtual(ordemDeProducao)) 
        //     throw new OrdemDeProducaoExpedidaException(ordemDeProducao);
    }

    @Transactional
    public void updateAll(List<ExpedicaoRequestDTO> expedicao) {
        for(ExpedicaoRequestDTO e : expedicao) {
            assignOrdemAtPosicao(e.ordemDeProducao(), e.posicaoFisica());
        }  
    }

    @Transactional(readOnly = true)
    public Expedicao findFirstPosicaoLivre() {
        return expedicaoRepository.findFirstByOrdemDeProducaoAtualOrderByPosicaoFisicaAsc(0)
                .orElseThrow(() -> new ExpedicaoLotadaException());
    }
}