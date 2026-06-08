package com.smart.appsa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.exception.DuplicateAndarException;
import com.smart.appsa.exception.EstoqueInsuficienteException;
import com.smart.appsa.exception.InvalidOrdemDeProducaoException;
import com.smart.appsa.exception.RequiredFieldException;
import com.smart.appsa.exception.TipoIncompativelComBlocosException;
import com.smart.appsa.exception.core.ResourceNotFoundException;
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
    void deveCriarPedidoComCamposValidos() {
        Pedido pedido = createPedido();
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(estoqueService.findByCorEstoque(CorEstoque.AZUL))
            .thenReturn(List.of(createEstoque(1L, CorEstoque.AZUL)));
        when(expedicaoService.findFirstPosicaoLivre()).thenReturn(createExpedicao());

        PedidoResponseDTO response = pedidoService.create(PedidoMapper.mapRequestDto(pedido));

        assertNotNull(response);
        assertEquals(pedido.getId(), response.id());
        assertEquals(pedido.getOrdemDeProducao(), response.ordemDeProducao());
        assertEquals(pedido.getStatus(), response.status());
        assertEquals(pedido.getTipo(), response.tipo());
        assertEquals(pedido.getCorTampa(), response.corTampa());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void deveChamarBlocoServiceParaCadaBlocoAoCriarPedido() {
        Pedido pedido = createPedido();
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(estoqueService.findByCorEstoque(CorEstoque.AZUL))
            .thenReturn(List.of(createEstoque(1L, CorEstoque.AZUL)));
        when(expedicaoService.findFirstPosicaoLivre()).thenReturn(createExpedicao());

        pedidoService.create(PedidoMapper.mapRequestDto(pedido));

        verify(blocoService, times(pedido.getBlocos().size())).create(any(Bloco.class));
    }

    @Test
    void deveAtribuirEstoqueVazioAposAssociarBlocoAoCriarPedido() {
        Pedido pedido = createPedido();
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(estoqueService.findByCorEstoque(CorEstoque.AZUL))
            .thenReturn(List.of(createEstoque(1L, CorEstoque.AZUL)));
        when(expedicaoService.findFirstPosicaoLivre()).thenReturn(createExpedicao());

        pedidoService.create(PedidoMapper.mapRequestDto(pedido));

        verify(estoqueService, times(1)).assignBlockColor(anyLong(), any(CorEstoque.class));
    }

    @Test
    void deveRetornarRequiredFieldExceptionQuandoCriarPedidoComBlocosNulos() {
        Pedido pedido = createPedido();
        pedido.setBlocos(null);
        PedidoRequestDTO request = PedidoMapper.mapRequestDto(pedido);

        assertThrows(RequiredFieldException.class, () -> pedidoService.create(request));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveRetornarRequiredFieldExceptionQuandoCriarPedidoComBlocosVazios() {
        Pedido pedido = createPedido();
        pedido.setBlocos(List.of());
        PedidoRequestDTO request = PedidoMapper.mapRequestDto(pedido);

        assertThrows(RequiredFieldException.class, () -> pedidoService.create(request));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveRetornarRequiredFieldExceptionQuandoCriarPedidoComStatusNulo() {
        Pedido pedido = createPedido();
        pedido.setStatus(null);
        PedidoRequestDTO request = PedidoMapper.mapRequestDto(pedido);

        assertThrows(RequiredFieldException.class, () -> pedidoService.create(request));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveRetornarRequiredFieldExceptionQuandoCriarPedidoComCorTampaNula() {
        Pedido pedido = createPedido();
        pedido.setCorTampa(null);
        PedidoRequestDTO request = PedidoMapper.mapRequestDto(pedido);

        assertThrows(RequiredFieldException.class, () -> pedidoService.create(request));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveRetornarInvalidOrdemDeProducaoExceptionQuandoCriarPedidoComOrdemZero() {
        Pedido pedido = createPedido();
        pedido.setOrdemDeProducao(0);
        PedidoRequestDTO request = PedidoMapper.mapRequestDto(pedido);

        assertThrows(InvalidOrdemDeProducaoException.class, () -> pedidoService.create(request));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveRetornarInvalidOrdemDeProducaoExceptionQuandoCriarPedidoComOrdemNegativa() {
        Pedido pedido = createPedido();
        pedido.setOrdemDeProducao(-5);
        PedidoRequestDTO request = PedidoMapper.mapRequestDto(pedido);

        assertThrows(InvalidOrdemDeProducaoException.class, () -> pedidoService.create(request));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveRetornarTipoIncompativelComBlocosExceptionQuandoQuantidadeDeBlocksNaoCorresponderAoTipo() {
        Pedido pedido = createPedido();
        pedido.setBlocos(List.of(
            createBloco(pedido, AndarBloco.PRIMEIRO),
            createBloco(pedido, AndarBloco.SEGUNDO)
        ));

        assertThrows(TipoIncompativelComBlocosException.class,
            () -> pedidoService.create(PedidoMapper.mapRequestDto(pedido)));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveRetornarDuplicateAndarExceptionQuandoCriarPedidoComBlocosDeAndarDuplicado() {
        Pedido pedido = createPedido();
        pedido.setTipo(TipoPedido.DUPLO);
        pedido.setBlocos(List.of(
            createBloco(pedido, AndarBloco.PRIMEIRO),
            createBloco(pedido, AndarBloco.PRIMEIRO)
        ));

        assertThrows(DuplicateAndarException.class,
            () -> pedidoService.create(PedidoMapper.mapRequestDto(pedido)));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void deveRetornarEstoqueInsuficienteExceptionQuandoNaoHouverEstoqueDisponivel() {
        Pedido pedido = createPedido();
        when(expedicaoService.findFirstPosicaoLivre()).thenReturn(createExpedicao());
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(estoqueService.findByCorEstoque(any(CorEstoque.class))).thenReturn(List.of());

        assertThrows(EstoqueInsuficienteException.class,
            () -> pedidoService.create(PedidoMapper.mapRequestDto(pedido)));
        verify(blocoService, never()).create(any());
    }

    @Test
    void deveRetornarPedidoQuandoBuscarPorIdValido() {
        Pedido pedido = createPedido();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoResponseDTO response = pedidoService.findById(1L);

        assertNotNull(response);
        assertEquals(pedido.getId(), response.id());
        verify(pedidoRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarResourceNotFoundExceptionQuandoBuscarPedidoComIdInvalido() {
        Long id = 99L;
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.findById(id));
        verify(pedidoRepository, times(1)).findById(id);
    }

    @Test
    void deveRetornarListaDePedidosQuandoBuscarTodos() {
        List<Pedido> pedidos = List.of(createPedido(), createPedido());
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        List<PedidoResponseDTO> result = pedidoService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremPedidos() {
        when(pedidoRepository.findAll()).thenReturn(List.of());

        List<PedidoResponseDTO> result = pedidoService.findAll();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void deveAtualizarStatusParaConcluidoQuandoIdValido() {
        Pedido pedido = createPedido();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        pedidoService.updateStatusAsCompleted(1L);

        verify(pedidoRepository, times(1)).findById(1L);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        assertEquals(StatusPedido.CONCLUIDO, pedido.getStatus());
    }

    @Test
    void devePreencherRegistroEntradaExpedicaoAoAtualizarStatusParaConcluido() {
        Pedido pedido = createPedido();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        pedidoService.updateStatusAsCompleted(1L);

        assertNotNull(pedido.getRegistroEntradaExpedicao());
    }

    @Test
    void deveRetornarResourceNotFoundExceptionQuandoAtualizarStatusDePedidoComIdInvalido() {
        Long id = 99L;
        when(pedidoRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> pedidoService.updateStatusAsCompleted(id));
        verify(pedidoRepository, never()).save(any());
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

        pedido.setBlocos(List.of(createBloco(pedido, AndarBloco.PRIMEIRO)));
        return pedido;
    }

    private Bloco createBloco(Pedido pedido, AndarBloco andar) {
        return Bloco.builder()
            .id(1L)
            .pedido(pedido)
            .laminas(null)
            .estoque(Estoque.builder().id(1L).build())
            .cor(CorBloco.AZUL)
            .andar(andar)
            .build();
    }

    private Estoque createEstoque(Long id, CorEstoque cor) {
        return Estoque.builder()
            .id(id)
            .corEstoque(cor)
            .posicaoFisica(1)
            .build();
    }

    private Expedicao createExpedicao() {
        return Expedicao.builder()
            .id(1L)
            .posicaoFisica(4)
            .ordemDeProducaoAtual(0)
            .build();
    }
}