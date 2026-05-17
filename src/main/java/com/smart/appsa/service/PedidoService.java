package com.smart.appsa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.exception.DuplicateAndarException;
import com.smart.appsa.exception.EstoqueInsuficienteException;
import com.smart.appsa.exception.InvalidOrdemDeProducaoException;
import com.smart.appsa.exception.RequiredFieldException;
import com.smart.appsa.exception.ResourceNotFoundException;
import com.smart.appsa.exception.TipoIncompativelComBlocosException;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.AndarBloco;
import com.smart.appsa.model.enums.CorBloco;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final BlocoService blocoService;
    private final EstoqueService estoqueService;
    private final ExpedicaoService expedicaoService;

    @Transactional
    public PedidoResponseDTO create(PedidoRequestDTO requestDTO) {
        validatePedido(requestDTO);
        Pedido pedido = PedidoMapper.mapEntityByRequestDTO(requestDTO);
        pedido.setRegistroCriacao(LocalDateTime.now());
        Pedido saved = saveWithExpedition(pedido);
        createBlocks(saved, requestDTO.blocos());
        return PedidoMapper.mapDto(pedidoRepository.findById(saved.getId()).get());
    }

    private void validatePedido(PedidoRequestDTO requestDTO) {
        validateRequiredFields(requestDTO);
        validateBusinessRules(requestDTO);
    }

    private void validateRequiredFields(PedidoRequestDTO requestDTO) {
        if (requestDTO == null) 
            throw new RequiredFieldException("PedidoRequestDTO");
        if (requestDTO.blocos() == null || requestDTO.blocos().isEmpty()) 
            throw new RequiredFieldException("Blocos");
        if (requestDTO.status() == null) 
            throw new RequiredFieldException("Status");
        if (requestDTO.tipo() == null) 
            throw new RequiredFieldException("Tipo Pedido");
        if (requestDTO.corTampa() == null) 
            throw new RequiredFieldException("Cor da tampa");
    }

    private void validateBusinessRules(PedidoRequestDTO requestDTO) {
        if (requestDTO.ordemDeProducao() <= 0) 
            throw new InvalidOrdemDeProducaoException(requestDTO.ordemDeProducao());
        if (requestDTO.tipo().getValue() != requestDTO.blocos().size()) 
            throw new TipoIncompativelComBlocosException(requestDTO.tipo(), requestDTO.blocos().size());
        validateDuplicateAndar(requestDTO.blocos());
        validateColorInventory(requestDTO.blocos());
    }

    private void validateDuplicateAndar(List<Bloco> blocos) {
        Map<AndarBloco, Long> countByAndar = blocos.stream()
            .collect(Collectors.groupingBy(
                Bloco::getAndar,
                Collectors.counting()
            ));

        List<AndarBloco> duplicateAndar = countByAndar.entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(Map.Entry::getKey)
            .toList();

        if (!duplicateAndar.isEmpty())
            throw new DuplicateAndarException(duplicateAndar);
    }

    private void validateColorInventory(List<Bloco> blocos) {
        Map<CorBloco, Long> contagemPorCor = blocos.stream()
            .collect(Collectors.groupingBy(
                Bloco::getCor,
                Collectors.counting()
            ));
    
        for (Map.Entry<CorBloco, Long> entry : contagemPorCor.entrySet()) {
            CorBloco cor = entry.getKey();
            long quantidadeNecessaria = entry.getValue();
            long quantidadeEmEstoque = estoqueService.countByCorEstoque(CorEstoque.valueOf(cor.name()));
            
            if (quantidadeEmEstoque < quantidadeNecessaria) 
                throw new EstoqueInsuficienteException(cor.toString(), quantidadeNecessaria, quantidadeEmEstoque);
            
        }
    }

    private Pedido saveWithExpedition(Pedido pedido) {
        Expedicao nextFree = expedicaoService.findFirstPosicaoLivre();
        pedido.setExpedicao(nextFree);
        expedicaoService.AssignOrdemAtPosicao(pedido.getOrdemDeProducao(), nextFree.getPosicaoFisica());
        return pedidoRepository.save(pedido);
    }

    private void createBlocks(Pedido pedido, List<Bloco> blocos) {
        for (Bloco bloco : blocos) {
            bloco.setPedido(pedido);
            blocoService.create(bloco);
        }
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO findById(Long id) {
        return PedidoMapper.mapDto(pedidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido", id)));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAll() {
        return pedidoRepository.findAll()
            .stream()
            .map(p -> PedidoMapper.mapDto(p))
            .toList();
    }

    @Transactional
    public void updateStatusAsCompleted(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
        pedido = prepareForCompletion(pedido);
        pedidoRepository.save(pedido);
    }

    private Pedido prepareForCompletion(Pedido pedido) {
        pedido.setStatus(StatusPedido.CONCLUIDO);
        pedido.setRegistroEntradaExpedicao(LocalDateTime.now());
        return pedido;
    }
}       
