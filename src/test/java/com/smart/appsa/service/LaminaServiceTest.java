package com.smart.appsa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smart.appsa.exception.LaminaPosicaoOcupadaException;
import com.smart.appsa.exception.RequiredFieldException;
import com.smart.appsa.exception.ResourceNotFoundException;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.enums.CorLamina;
import com.smart.appsa.model.enums.PadraoLamina;
import com.smart.appsa.model.enums.PosicaoLamina;
import com.smart.appsa.repository.LaminaRepository;

@ExtendWith(MockitoExtension.class)
public class LaminaServiceTest {

    @Mock
    private LaminaRepository laminaRepository;

    @InjectMocks
    private LaminaService laminaService;

    // ─── CRUD ────────────────────────────────────────────────────────────────────

    @Test
    public void deveRetornarTodasAsLaminas() {
        // Arrange
        Lamina l1 = createLamina(1L, CorLamina.AZUL, PadraoLamina.NENHUM, PosicaoLamina.ESQUERDA);
        Lamina l2 = createLamina(2L, CorLamina.VERMELHO, PadraoLamina.CASA, PosicaoLamina.FRENTE);
        when(laminaRepository.findAll()).thenReturn(List.of(l1, l2));

        // Act
        List<Lamina> result = laminaService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(laminaRepository, times(1)).findAll();
    }

    @Test
    public void deveRetornarLaminaPorId() {
        // Arrange
        Lamina lamina = createLamina(1L, CorLamina.AZUL, PadraoLamina.NENHUM, PosicaoLamina.ESQUERDA);
        when(laminaRepository.findById(1L)).thenReturn(Optional.of(lamina));

        // Act
        Lamina result = laminaService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(CorLamina.AZUL, result.getCor());
    }

    @Test
    public void deveRetornarLaminasPorBlocoId() {
        // Arrange
        Lamina l1 = createLamina(1L, CorLamina.AZUL, PadraoLamina.NENHUM, PosicaoLamina.ESQUERDA);
        when(laminaRepository.findByBlocoId(1L)).thenReturn(List.of(l1));

        // Act
        List<Lamina> result = laminaService.findByBlocoId(1L);

        // Assert
        assertEquals(1, result.size());
        verify(laminaRepository, times(1)).findByBlocoId(1L);
    }

    @Test
    public void deveCriarLaminaComCamposValidos() {
        // Arrange
        Lamina lamina = createLaminaComBloco(1L, CorLamina.AZUL, PadraoLamina.NENHUM, PosicaoLamina.ESQUERDA);
        when(laminaRepository.save(any(Lamina.class))).thenReturn(lamina);

        // Act
        Lamina result = laminaService.create(lamina);

        // Assert
        assertNotNull(result);
        assertEquals(CorLamina.AZUL, result.getCor());
        verify(laminaRepository, times(1)).save(any(Lamina.class));
    }

    @Test
    public void deveAtualizarLaminaComPosicaoDisponivel() {
        // Arrange
        Lamina laminaExistente = createLaminaComBloco(1L, CorLamina.AZUL, PadraoLamina.NENHUM, PosicaoLamina.ESQUERDA);
        Lamina laminaAtualizada = createLaminaComBloco(1L, CorLamina.VERMELHO, PadraoLamina.CASA, PosicaoLamina.FRENTE);

        when(laminaRepository.findById(1L)).thenReturn(Optional.of(laminaExistente));
        when(laminaRepository.findByBloco(laminaExistente.getBloco())).thenReturn(List.of(laminaExistente));
        when(laminaRepository.save(any(Lamina.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Lamina result = laminaService.update(1L, laminaAtualizada);

        // Assert
        assertEquals(CorLamina.VERMELHO, result.getCor());
        assertEquals(PosicaoLamina.FRENTE, result.getPosicao());
        verify(laminaRepository, times(1)).save(any(Lamina.class));
    }

    @Test
    public void deveDeletarLaminaExistente() {
        // Arrange
        when(laminaRepository.existsById(1L)).thenReturn(true);

        // Act
        laminaService.delete(1L);

        // Assert
        verify(laminaRepository, times(1)).deleteById(1L);
    }

    // ─── FALHA — CAMPOS FALTANDO ─────────────────────────────────────────────────

    @Test
    public void deveLancarExcecaoQuandoCorForNula() {
        // Arrange
        Lamina lamina = createLaminaComBloco(1L, null, PadraoLamina.NENHUM, PosicaoLamina.ESQUERDA);

        // Act & Assert
        assertThrows(RequiredFieldException.class, () -> laminaService.create(lamina));
    }

    @Test
    public void deveLancarExcecaoQuandoPadraoForNulo() {
        // Arrange
        Lamina lamina = createLaminaComBloco(1L, CorLamina.AZUL, null, PosicaoLamina.ESQUERDA);

        // Act & Assert
        assertThrows(RequiredFieldException.class, () -> laminaService.create(lamina));
    }

    @Test
    public void deveLancarExcecaoQuandoPosicaoForNula() {
        // Arrange
        Lamina lamina = createLaminaComBloco(1L, CorLamina.AZUL, PadraoLamina.NENHUM, null);

        // Act & Assert
        assertThrows(RequiredFieldException.class, () -> laminaService.create(lamina));
    }

    @Test
    public void deveLancarExcecaoQuandoBlocoForNulo() {
        // Arrange
        Lamina lamina = createLamina(1L, CorLamina.AZUL, PadraoLamina.NENHUM, PosicaoLamina.ESQUERDA);

        // Act & Assert
        assertThrows(RequiredFieldException.class, () -> laminaService.create(lamina));
    }

    // ─── FALHA — REGRAS DE NEGÓCIO ───────────────────────────────────────────────

    @Test
    public void deveLancarExcecaoAoBuscarLaminaIdInexistente() {
        // Arrange
        when(laminaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> laminaService.findById(99L));
    }

    @Test
    public void deveLancarExcecaoAoDeletarLaminaInexistente() {
        // Arrange
        when(laminaRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> laminaService.delete(99L));
    }

    @Test
    public void deveLancarExcecaoQuandoPosicaoJaOcupadaNoBloco() {
        // Arrange
        Bloco bloco = Bloco.builder().id(1L).build();

        Lamina laminaExistente = Lamina.builder()
                .id(1L)
                .cor(CorLamina.AZUL)
                .padrao(PadraoLamina.NENHUM)
                .posicao(PosicaoLamina.ESQUERDA)
                .bloco(bloco)
                .build();

        Lamina outraLamina = Lamina.builder()
                .id(2L)
                .cor(CorLamina.VERMELHO)
                .padrao(PadraoLamina.CASA)
                .posicao(PosicaoLamina.ESQUERDA) // mesma posição — deve lançar exceção
                .bloco(bloco)
                .build();

        when(laminaRepository.findById(1L)).thenReturn(Optional.of(laminaExistente));
        when(laminaRepository.findByBloco(bloco)).thenReturn(List.of(laminaExistente, outraLamina));

        // Act & Assert
        assertThrows(LaminaPosicaoOcupadaException.class,
                () -> laminaService.update(1L, outraLamina));
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────────

    private Lamina createLamina(Long id, CorLamina cor, PadraoLamina padrao, PosicaoLamina posicao) {
        return Lamina.builder()
                .id(id)
                .cor(cor)
                .padrao(padrao)
                .posicao(posicao)
                .build();
    }

    private Lamina createLaminaComBloco(Long id, CorLamina cor, PadraoLamina padrao, PosicaoLamina posicao) {
        return Lamina.builder()
                .id(id)
                .cor(cor)
                .padrao(padrao)
                .posicao(posicao)
                .bloco(Bloco.builder().id(1L).build())
                .build();
    }
}