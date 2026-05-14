package com.smart.appsa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.CorBloco;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private PedidoRepository pedidoRepository;
    private BlocoService blocoService;
    private EstoqueService estoqueService;

    @Transactional
    public PedidoResponseDTO create(PedidoRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("PedidoRequestDTO não pode ser nulo");
        }
        if (requestDTO.ordemDeProducao() <= 0) {
            throw new IllegalArgumentException("Ordem de produção deve ser maior que zero");
        }
        if (requestDTO.blocos() == null || requestDTO.blocos().isEmpty()) {
            throw new IllegalArgumentException("A lista de blocos não pode ser nula ou vazia, deve conter pelo menos um bloco");
        }
        if (requestDTO.status() == null) {
            throw new IllegalArgumentException("Status do pedido não pode ser nulo");
        }
        if (requestDTO.tipo() == null) {
            throw new IllegalArgumentException("Tipo do pedido não pode ser nulo");
        }
        if (requestDTO.corTampa() == null) {
            throw new IllegalArgumentException("Cor da tampa não pode ser nula");
        }
        if (requestDTO.tipo().getValue() != requestDTO.blocos().size()) {
            throw new IllegalArgumentException("O tipo do pedido deve ser compatível com a quantidade de blocos");
        }
        validarEstoqueParaCores(requestDTO.blocos());
        Pedido pedido = mapEntityByRequestDTO(requestDTO);
        for (Bloco bloco : pedido.getBlocos()) {
            bloco.setPedido(pedido);
            pedido.getBlocos().add(blocoService.create(bloco));
        }
        pedido.setRegistroCriacao(java.time.LocalDateTime.now());
        return mapDto(pedidoRepository.save(pedido));
    }

    private void validarEstoqueParaCores(List<Bloco> blocos) {
        // Agrupa blocos por cor e conta quantos de cada cor
        Map<CorBloco, Long> contagemPorCor = blocos.stream()
            .collect(Collectors.groupingBy(
                Bloco::getCor,
                Collectors.counting()
            ));
        
        // Para cada cor distinta, valida se tem estoque suficiente
        for (Map.Entry<CorBloco, Long> entry : contagemPorCor.entrySet()) {
            CorBloco cor = entry.getKey();
            long quantidadeNecessaria = entry.getValue();
            long quantidadeEmEstoque = estoqueService.countByCorEstoque(CorEstoque.valueOf(cor.name()));
            
            if (quantidadeEmEstoque < quantidadeNecessaria) {
                throw new IllegalArgumentException(
                    String.format("Estoque insuficiente para a cor %s. Necessário: %d, Disponível: %d",
                        cor, quantidadeNecessaria, quantidadeEmEstoque)
                );
            }
        }
    }

    public PedidoResponseDTO findById(Long id) {
        return mapDto(pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + id)));
    }

    public List<PedidoResponseDTO> findAll() {
        return pedidoRepository.findAll()
            .stream()
            .map(p -> mapDto(p))
            .toList();
    }

    public void atualizarStatusParaConcluido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com id: " + id));
        pedido.setStatus(StatusPedido.CONCLUIDO);
        pedido.setRegistroEntradaExpedicao(LocalDateTime.now());
        pedidoRepository.save(pedido);
    }

    private PedidoResponseDTO mapDto(Pedido pedido) {
        return PedidoResponseDTO.builder()
            .id(pedido.getId())
            .ordemDeProducao(pedido.getOrdemDeProducao())
            .blocos(pedido.getBlocos())
            .status(pedido.getStatus())
            .tipo(pedido.getTipo())
            .corTampa(pedido.getCorTampa())
            .registroCriacao(pedido.getRegistroCriacao())
            .registroEntradaExpedicao(pedido.getRegistroEntradaExpedicao())
            .registroSaidaExpedicao(pedido.getRegistroSaidaExpedicao())
            .build();
    }

    private Pedido mapEntityByRequestDTO(PedidoRequestDTO requestDTO) {
        return Pedido.builder()
            .ordemDeProducao(requestDTO.ordemDeProducao())
            .blocos(requestDTO.blocos())
            .status(requestDTO.status())
            .tipo(requestDTO.tipo())
            .corTampa(requestDTO.corTampa())
            .build();
    }
}       
