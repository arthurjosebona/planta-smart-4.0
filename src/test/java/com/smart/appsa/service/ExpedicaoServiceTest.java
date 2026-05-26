package com.smart.appsa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.smart.appsa.dto.response.ExpedicaoResponseDTO;
import com.smart.appsa.exception.ExpedicaoLotadaException;
import com.smart.appsa.exception.InvalidPosicaoExpedicaoException;
import com.smart.appsa.exception.OrdemDeProducaoExpedidaException;
import com.smart.appsa.exception.PosicaoExpedicaoOcupadaException;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.repository.ExpedicaoRepository;

public class ExpedicaoServiceTest {

        @Mock
    private ExpedicaoRepository expedicaoRepository;
 
    @InjectMocks
    private ExpedicaoService expedicaoService;
 
    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------
 
    @Test
    public void deveRetornarListaDeExpedicoes() {
        // Arrange
        Expedicao expedicao1 = createExpedicao(1L, 1, 10);
        Expedicao expedicao2 = createExpedicao(2L, 2, 20);
        when(expedicaoRepository.findAll()).thenReturn(List.of(expedicao1, expedicao2));
 
        // Act
        List<ExpedicaoResponseDTO> response = expedicaoService.findAll();
 
        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());
        verify(expedicaoRepository, times(1)).findAll();
    }
 
    @Test
    public void deveRetornarListaVaziaQuandoNaoHouverExpedicoes() {
        // Arrange
        when(expedicaoRepository.findAll()).thenReturn(List.of());
 
        // Act
        List<ExpedicaoResponseDTO> response = expedicaoService.findAll();
 
        // Assert
        assertNotNull(response);
        assertEquals(0, response.size());
        verify(expedicaoRepository, times(1)).findAll();
    }
 
    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------
 
    @Test
    public void deveRetornarExpedicaoPorId() {
        // Arrange
        Expedicao expedicao = createExpedicao(1L, 1, 10);
        when(expedicaoRepository.findById(1L)).thenReturn(Optional.of(expedicao));
 
        // Act
        ExpedicaoResponseDTO response = expedicaoService.findById(1L);
 
        // Assert
        assertNotNull(response);
        assertEquals(expedicao.getId(), response.id());
        verify(expedicaoRepository, times(1)).findById(1L);
    }
 
    @Test
    public void deveLancarExcecaoQuandoIdNaoEncontrado() {
        // Arrange
        when(expedicaoRepository.findById(99L)).thenReturn(Optional.empty());
 
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> expedicaoService.findById(99L));
        verify(expedicaoRepository, times(1)).findById(99L);
    }
 
    // -------------------------------------------------------------------------
    // findByOrdemDeProducao
    // -------------------------------------------------------------------------
 
    @Test
    public void deveRetornarExpedicaoPorOrdemDeProducao() {
        // Arrange
        Expedicao expedicao = createExpedicao(1L, 1, 42);
        when(expedicaoRepository.findByOrdemDeProducaoAtual(42)).thenReturn(Optional.of(expedicao));
 
        // Act
        Expedicao response = expedicaoService.findByOrdemDeProducao(42);
 
        // Assert
        assertNotNull(response);
        assertEquals(42, response.getOrdemDeProducaoAtual());
        verify(expedicaoRepository, times(1)).findByOrdemDeProducaoAtual(42);
    }
 
    @Test
    public void deveLancarExcecaoQuandoOrdemDeProducaoNaoEncontrada() {
        // Arrange
        when(expedicaoRepository.findByOrdemDeProducaoAtual(99)).thenReturn(Optional.empty());
 
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> expedicaoService.findByOrdemDeProducao(99));
        verify(expedicaoRepository, times(1)).findByOrdemDeProducaoAtual(99);
    }
 
    // -------------------------------------------------------------------------
    // AssignOrdemAtPosicao
    // -------------------------------------------------------------------------
 
    @Test
    public void deveAtribuirOrdemNaPosicaoComCamposValidos() {
        // Arrange
        int ordemDeProducao = 10;
        int posicaoFisica = 5;
        Expedicao expedicao = createExpedicao(1L, posicaoFisica, 0);
 
        when(expedicaoRepository.findPosicoesOcupadas()).thenReturn(List.of());
        when(expedicaoRepository.existsByOrdemDeProducaoAtual(ordemDeProducao)).thenReturn(false);
        when(expedicaoRepository.findByPosicaoFisica(posicaoFisica)).thenReturn(Optional.of(expedicao));
        when(expedicaoRepository.save(any(Expedicao.class))).thenReturn(expedicao);
 
        // Act
        expedicaoService.AssignOrdemAtPosicao(ordemDeProducao, posicaoFisica);
 
        // Assert
        verify(expedicaoRepository, times(1)).save(any(Expedicao.class));
    }
 
    @Test
    public void deveLancarExcecaoQuandoPosicaoFisicaMenorQueUm() {
        // Arrange
        int ordemDeProducao = 10;
        int posicaoFisicaInvalida = 0;
 
        // Act & Assert
        assertThrows(InvalidPosicaoExpedicaoException.class,
                () -> expedicaoService.AssignOrdemAtPosicao(ordemDeProducao, posicaoFisicaInvalida));
        verify(expedicaoRepository, never()).save(any(Expedicao.class));
    }
 
    @Test
    public void deveLancarExcecaoQuandoPosicaoFisicaMaiorQueDoze() {
        // Arrange
        int ordemDeProducao = 10;
        int posicaoFisicaInvalida = 13;
 
        // Act & Assert
        assertThrows(InvalidPosicaoExpedicaoException.class,
                () -> expedicaoService.AssignOrdemAtPosicao(ordemDeProducao, posicaoFisicaInvalida));
        verify(expedicaoRepository, never()).save(any(Expedicao.class));
    }
 
    @Test
    public void deveLancarExcecaoQuandoPosicaoJaEstaOcupada() {
        // Arrange
        int ordemDeProducao = 10;
        int posicaoOcupada = 3;
 
        when(expedicaoRepository.findPosicoesOcupadas()).thenReturn(List.of(posicaoOcupada));
 
        // Act & Assert
        assertThrows(PosicaoExpedicaoOcupadaException.class,
                () -> expedicaoService.AssignOrdemAtPosicao(ordemDeProducao, posicaoOcupada));
        verify(expedicaoRepository, never()).save(any(Expedicao.class));
    }
 
    @Test
    public void deveLancarExcecaoQuandoOrdemDeProducaoJaFoiExpedida() {
        // Arrange
        int ordemJaExpedida = 10;
        int posicaoFisica = 5;
 
        when(expedicaoRepository.findPosicoesOcupadas()).thenReturn(List.of());
        when(expedicaoRepository.existsByOrdemDeProducaoAtual(ordemJaExpedida)).thenReturn(true);
 
        // Act & Assert
        assertThrows(OrdemDeProducaoExpedidaException.class,
                () -> expedicaoService.AssignOrdemAtPosicao(ordemJaExpedida, posicaoFisica));
        verify(expedicaoRepository, never()).save(any(Expedicao.class));
    }
 
    @Test
    public void deveLancarRuntimeExceptionQuandoPosicaoNaoEncontradaNoBanco() {
        // Arrange
        int ordemDeProducao = 10;
        int posicaoFisica = 5;
 
        when(expedicaoRepository.findPosicoesOcupadas()).thenReturn(List.of());
        when(expedicaoRepository.existsByOrdemDeProducaoAtual(ordemDeProducao)).thenReturn(false);
        when(expedicaoRepository.findByPosicaoFisica(posicaoFisica)).thenReturn(Optional.empty());
 
        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> expedicaoService.AssignOrdemAtPosicao(ordemDeProducao, posicaoFisica));
        verify(expedicaoRepository, never()).save(any(Expedicao.class));
    }
 
    // -------------------------------------------------------------------------
    // findFirstPosicaoLivre
    // -------------------------------------------------------------------------
 
    @Test
    public void deveRetornarPrimeiraPosicaoLivre() {
        // Arrange
        Expedicao expedicaoLivre = createExpedicao(1L, 2, 0);
        when(expedicaoRepository.findFirstByOrdemDeProducaoAtualOrderByPosicaoFisicaAsc(0))
                .thenReturn(Optional.of(expedicaoLivre));
 
        // Act
        Expedicao response = expedicaoService.findFirstPosicaoLivre();
 
        // Assert
        assertNotNull(response);
        assertEquals(0, response.getOrdemDeProducaoAtual());
        assertEquals(2, response.getPosicaoFisica());
        verify(expedicaoRepository, times(1))
                .findFirstByOrdemDeProducaoAtualOrderByPosicaoFisicaAsc(0);
    }
 
    @Test
    public void deveLancarExpedicaoLotadaExceptionQuandoNaoHouverPosicaoLivre() {
        // Arrange
        when(expedicaoRepository.findFirstByOrdemDeProducaoAtualOrderByPosicaoFisicaAsc(0))
                .thenReturn(Optional.empty());
 
        // Act & Assert
        assertThrows(ExpedicaoLotadaException.class, () -> expedicaoService.findFirstPosicaoLivre());
        verify(expedicaoRepository, times(1))
                .findFirstByOrdemDeProducaoAtualOrderByPosicaoFisicaAsc(0);
    }
 
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private Expedicao createExpedicao(Long id, int posicaoFisica, int ordemDeProducaoAtual) {
        return Expedicao.builder()
                .id(id)
                .posicaoFisica(posicaoFisica)
                .ordemDeProducaoAtual(ordemDeProducaoAtual)
                .build();
    }

}
