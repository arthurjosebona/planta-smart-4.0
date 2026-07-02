package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.config.ClpIpConfig;
import com.smart.appsa.dto.request.ExpedicaoRequestDTO;
import com.smart.appsa.dto.response.ExpedicaoResponseDTO;
import com.smart.appsa.exception.ExpedicaoLotadaException;
import com.smart.appsa.exception.InvalidPosicaoExpedicaoException;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.mapper.ExpedicaoMapper;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.repository.ExpedicaoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedicaoService {
    // Data Block da estação EXPEDIÇÃO.
    private static final int DB_EXPEDICAO = 9;
    // Offset (int) inicial do magazine de expedição (12 posições, 2 bytes cada).
    private static final int OFFSET_MAGAZINE = 6;
    // Número de posições do magazine de expedição.
    private static final int TAMANHO_MAGAZINE = 12;

    private final ExpedicaoRepository expedicaoRepository;
    private final PlcConnectionService plcConnectionService;
    private final ClpIpConfig clpIpConfig;
    private final AppStateConfig appStateConfig;

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
        escreverExpedicaoNoClp();
    }

    // Escreve o magazine completo de OPs no CLP EXPEDIÇÃO [DB9, byte 6, 24 bytes].
    // A verificação de readOnly é feita primeiro: em modo somente-leitura nada é escrito.
    private void escreverExpedicaoNoClp() {
        if (appStateConfig.isReadOnly()) {
            return;
        }

        String ip = clpIpConfig.getExpedicaoIp();
        PlcConnector connector = plcConnectionService.getConnection(ip);
        if (connector == null) {
            log.warn("Sem conexão com CLP EXPEDIÇÃO {} — magazine não escrito.", ip);
            return;
        }

        // Cada posição física (1..12) vira um int16 big-endian (offset = (posicao - 1) * 2).
        byte[] magazine = new byte[TAMANHO_MAGAZINE * 2];
        for (Expedicao e : expedicaoRepository.findAll()) {
            Integer posicao = e.getPosicaoFisica();
            if (posicao != null && posicao >= 1 && posicao <= TAMANHO_MAGAZINE) {
                int op = e.getOrdemDeProducaoAtual() != null ? e.getOrdemDeProducaoAtual() : 0;
                int idx = (posicao - 1) * 2;
                magazine[idx] = (byte) ((op >> 8) & 0xFF);
                magazine[idx + 1] = (byte) (op & 0xFF);
            }
        }

        try {
            connector.writeBlock(DB_EXPEDICAO, OFFSET_MAGAZINE, magazine.length, magazine);
            log.debug("Magazine da expedição escrito no CLP {} [DB9:6, {} bytes].", ip, magazine.length);
        } catch (Exception ex) {
            log.error("Erro ao escrever magazine da Expedição [DB9:6] no CLP {}: {}", ip, ex.getMessage(), ex);
        }
    }

    @Transactional(readOnly = true)
    public Expedicao findFirstPosicaoLivre() {
        return expedicaoRepository.findFirstByOrdemDeProducaoAtualOrderByPosicaoFisicaAsc(0)
                .orElseThrow(() -> new ExpedicaoLotadaException());
    }

    @Transactional(readOnly = true)
    public Expedicao findByPosicaoFisica(Integer posicaoFisica) {
        return expedicaoRepository.findByPosicaoFisica(posicaoFisica)
            .orElseThrow(() -> new ResourceNotFoundException("Expedicao", "posicaoFisica", posicaoFisica));
    }
}