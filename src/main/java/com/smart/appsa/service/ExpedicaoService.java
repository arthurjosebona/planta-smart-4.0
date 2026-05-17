package com.smart.appsa.service;

import com.smart.appsa.model.Expedicao;
import com.smart.appsa.repository.ExpedicaoRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.smart.appsa.exception.ExpedicaoLotadaException;
import com.smart.appsa.exception.InvalidPosicaoExpedicaoException;
import com.smart.appsa.exception.OrdemDeProducaoExpedidaException;
import com.smart.appsa.exception.PosicaoExpedicaoOcupadaException;
import com.smart.appsa.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class ExpedicaoService {
    private final ExpedicaoRepository expedicaoRepository;

    @Transactional(readOnly = true)
    public List<Expedicao> findAll() {
        return expedicaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Expedicao findById(Long id) {
        return expedicaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expedicao", id));
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
    public void AssignOrdemAtPosicao(int ordemDeProducao, int posicaoFisica) {
        validateFields(ordemDeProducao, posicaoFisica);
        Expedicao expedicao = expedicaoRepository.findByPosicaoFisica(posicaoFisica)
                .orElseThrow(() -> new RuntimeException("Posição não encontrada no banco."));
        expedicao.setOrdemDeProducaoAtual(ordemDeProducao);
        expedicaoRepository.save(expedicao);
    }

    private void validateFields(int ordemDeProducao, int posicaoFisica) {
        if (posicaoFisica < 1 || posicaoFisica > 12) 
            throw new InvalidPosicaoExpedicaoException(posicaoFisica);
        if (expedicaoRepository.findPosicoesOcupadas().contains(posicaoFisica)) 
            throw new PosicaoExpedicaoOcupadaException(posicaoFisica);
        if (expedicaoRepository.existsByOrdemDeProducaoAtual(ordemDeProducao)) 
            throw new OrdemDeProducaoExpedidaException(ordemDeProducao);
    }

    @Transactional(readOnly = true)
    public Expedicao findFirstPosicaoLivre() {
        return expedicaoRepository.findFirstByOrdemDeProducaoAtualOrderByPosicaoFisicaAsc(0)
                .orElseThrow(() -> new ExpedicaoLotadaException());
    }
}