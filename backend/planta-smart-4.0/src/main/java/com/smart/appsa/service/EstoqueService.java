package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.config.ClpIpConfig;
import com.smart.appsa.dto.request.EstoqueRequestDTO;
import com.smart.appsa.dto.response.EstoqueResponseDTO;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.mapper.EstoqueMapper;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.repository.EstoqueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EstoqueService {

    // Data Block da estação ESTOQUE.
    private static final int DB_ESTOQUE = 9;
    // Offset (byte) inicial do mapa de cores do magazine (28 posições, 1 byte cada).
    private static final int OFFSET_MAGAZINE = 68;
    // Número de posições do magazine de estoque.
    private static final int TAMANHO_MAGAZINE = 28;

    private final EstoqueRepository estoqueRepository;
    private final PlcConnectionService plcConnectionService;
    private final ClpIpConfig clpIpConfig;
    private final AppStateConfig appStateConfig;

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

    @Transactional(readOnly = true)
    public List<Estoque> findByCorEstoque(CorEstoque cor) {
        return estoqueRepository.findByCorEstoque(cor);
    }

    @Transactional
    public Estoque assignBlockColor(Long idEstoque, CorEstoque novaCor) {
        Estoque estoque = estoqueRepository.findById(idEstoque)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque", idEstoque));

        estoque.setCorEstoque(novaCor);
        return estoqueRepository.save(estoque);
    }

    @Transactional
    public Estoque assignBlockColorByPosicaoFisica(Integer posicaoFisica, CorEstoque novaCor) {
        Estoque estoque = estoqueRepository.findByPosicaoFisica(posicaoFisica)
                .orElseThrow(() -> new ResourceNotFoundException("Estoque", "posicaoFisica", posicaoFisica));

        estoque.setCorEstoque(novaCor);
        return estoqueRepository.save(estoque);
    }

    @Transactional
    public void updateAllEstoque(List<EstoqueRequestDTO> estoque) {
        for (EstoqueRequestDTO e : estoque) {
            assignBlockColor(e.id(), e.corEstoque());
        }
        escreverEstoqueNoClp();
    }

    // Escreve o mapa de cores completo do magazine no CLP ESTOQUE [DB9, byte 68, 28 bytes].
    // A verificação de readOnly é feita primeiro: em modo somente-leitura nada é escrito.
    private void escreverEstoqueNoClp() {
        if (appStateConfig.isReadOnly()) {
            return;
        }

        String ip = clpIpConfig.getEstoqueIp();
        PlcConnector connector = plcConnectionService.getConnection(ip);
        if (connector == null) {
            log.warn("Sem conexão com CLP ESTOQUE {} — magazine não escrito.", ip);
            return;
        }

        // Cada posição física (1..28) vira 1 byte com o valor da cor (offset = posicao - 1).
        byte[] magazine = new byte[TAMANHO_MAGAZINE];
        for (Estoque e : estoqueRepository.findAll()) {
            Integer posicao = e.getPosicaoFisica();
            if (posicao != null && posicao >= 1 && posicao <= TAMANHO_MAGAZINE) {
                magazine[posicao - 1] = (byte) e.getCorEstoque().getValue().intValue();
            }
        }

        try {
            connector.writeBlock(DB_ESTOQUE, OFFSET_MAGAZINE, TAMANHO_MAGAZINE, magazine);
            log.debug("Magazine do estoque escrito no CLP {} [DB9:68, {} bytes].", ip, TAMANHO_MAGAZINE);
        } catch (Exception ex) {
            log.error("Erro ao escrever magazine do Estoque [DB9:68] no CLP {}: {}", ip, ex.getMessage(), ex);
        }
    }
}