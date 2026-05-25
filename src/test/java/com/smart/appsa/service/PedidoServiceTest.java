package com.smart.appsa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.AndarBloco;
import com.smart.appsa.model.enums.CorBloco;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;
import com.smart.appsa.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {
    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private BlocoService blocoService;
    @Mock
    private EstoqueService estoqueService;
    @Mock
    private ExpedicaoService expedicaoService;
    @InjectMocks
    private PedidoService pedidoService;

    @Test
    public void deveCriarPedidoComCamposValidos() {
        // Arrange
        Pedido pedido = createPedido();
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        Estoque estoque = Estoque.builder()
            .id(1L)
            .corEstoque(CorEstoque.AZUL)
            .posicaoFisica(1)
            .build();
        when(estoqueService.findEntityById(1L)).thenReturn(estoque);
        Expedicao expedicao = Expedicao.builder()
            .id(1L)
            .posicaoFisica(4)
            .ordemDeProducaoAtual(0)
            .build();

        when(expedicaoService.findFirstPosicaoLivre()).thenReturn(expedicao);

        // Act
        PedidoResponseDTO response = pedidoService.create(PedidoMapper.mapRequestDto(pedido));

        // Assert
        assertEquals(pedido.getId(), response.id()); // Verifica campos individualmente porque retorna hash diferente (comportamento esperado)
        assertEquals(pedido.getOrdemDeProducao(), response.ordemDeProducao());
        assertEquals(pedido.getStatus(), response.status());
        assertEquals(pedido.getTipo(), response.tipo());
        assertEquals(pedido.getCorTampa(), response.corTampa());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    private Pedido createPedido() {
        Pedido pedido = Pedido.builder()
            .id(1L)
            .ordemDeProducao(1)
            .status(StatusPedido.PENDENTE)
            .tipo(TipoPedido.SIMPLES)
            .corTampa(CorTampa.VERMELHO)
            .registroCriacao(LocalDateTime.now())
            .registroEntradaExpedicao(null)
            .registroSaidaExpedicao(null)
            .build();

        Bloco bloco = createBloco(pedido);

        pedido.setBlocos(List.of(bloco));
        return pedido;
    }

    private Bloco createBloco(Pedido pedido) {
        return Bloco.builder()
            .id(1L)
            .pedido(pedido)
            .laminas(null)
            .estoque(Estoque.builder().id(1l).build())
            .cor(CorBloco.AZUL)
            .andar(AndarBloco.PRIMEIRO)
            .build();
    }
}
