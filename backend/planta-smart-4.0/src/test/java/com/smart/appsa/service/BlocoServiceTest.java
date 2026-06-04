package com.smart.appsa.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smart.appsa.exception.DuplicatePosicaoException;
import com.smart.appsa.exception.LaminasSizeException;
import com.smart.appsa.exception.RequiredFieldException;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.model.enums.AndarBloco;
import com.smart.appsa.model.enums.CorBloco;
import com.smart.appsa.model.enums.CorLamina;
import com.smart.appsa.model.enums.PadraoLamina;
import com.smart.appsa.model.enums.PosicaoLamina;
import com.smart.appsa.repository.BlocoRepository;

@ExtendWith(MockitoExtension.class)
public class BlocoServiceTest {

    @Mock
    private BlocoRepository blocoRepository;
    @Mock
    private LaminaService laminaService;
    @Mock
    private EstoqueService estoqueService;
    @InjectMocks
    private BlocoService blocoService;

    @Test
    void deveRetornarListaDeBlocosQuandoBuscarTodos() {
        // Arrange
        List<Bloco> blocos = List.of(createBloco(), createBloco());
        when(blocoRepository.findAll()).thenReturn(blocos);

        // Act
        List<Bloco> result = blocoService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(blocoRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremBlocos() {
        // Arrange
        when(blocoRepository.findAll()).thenReturn(List.of());

        // Act
        List<Bloco> result = blocoService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(blocoRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarBlocoQuandoBuscarPorIdValido() {
        // Arrange
        Bloco bloco = createBloco();
        when(blocoRepository.findById(1L)).thenReturn(Optional.of(bloco));

        // Act
        Bloco result = blocoService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(blocoRepository, times(1)).findById(1L);
    }

    @Test
    void deveRetornarResourceNotFoundExceptionQuandoBuscarBlocoComIdInvalido() {
        // Arrange
        Long id = 10L;
        when(blocoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> blocoService.findById(id));
        verify(blocoRepository, times(1)).findById(id);
    }

    @Test
    void deveCriarBlocoComCamposValidos() {
        // Arrange
        Bloco bloco = createBloco();
        Estoque estoque = Estoque.builder().id(1L).build();
        when(estoqueService.findEntityById(1L)).thenReturn(estoque);
        when(blocoRepository.save(any(Bloco.class))).thenReturn(bloco);

        // Act
        Bloco created = blocoService.create(bloco);

        // Assert
        assertNotNull(created);
        verify(blocoRepository, times(1)).save(any(Bloco.class));
        verify(laminaService, times(bloco.getLaminas().size())).create(any(Lamina.class));
    }

    @Test
    void deveRetornarLaminasSizeExceptionQuandoCriarBlocoComMaisDeTresLaminas() {
        // Arrange
        Bloco bloco = createBloco();
        bloco.setLaminas(List.of(
            createLamina(1L, CorLamina.AZUL,    PadraoLamina.CASA,     PosicaoLamina.DIREITA),
            createLamina(2L, CorLamina.VERMELHO, PadraoLamina.ESTRELA,  PosicaoLamina.ESQUERDA),
            createLamina(3L, CorLamina.VERDE,    PadraoLamina.NAVIO, PosicaoLamina.FRENTE),
            createLamina(4L, CorLamina.AZUL,     PadraoLamina.CASA,     PosicaoLamina.FRENTE)
        ));

        // Act & Assert
        assertThrows(LaminasSizeException.class, () -> blocoService.create(bloco));
        verify(blocoRepository, never()).save(any());
    }

    @Test
    void deveRetornarDuplicatePosicaoExceptionQuandoCriarBlocoComLaminasDePosicaoDuplicada() {
        // Arrange
        Bloco bloco = createBloco();
        bloco.setLaminas(List.of(
            createLamina(1L, CorLamina.AZUL,    PadraoLamina.CASA,    PosicaoLamina.DIREITA),
            createLamina(2L, CorLamina.VERMELHO, PadraoLamina.ESTRELA, PosicaoLamina.DIREITA)
        ));

        // Act & Assert
        assertThrows(DuplicatePosicaoException.class, () -> blocoService.create(bloco));
        verify(blocoRepository, never()).save(any());
    }

    @Test
    void deveCriarBlocoComTresLaminasEmPosicoesDistintas() {
        // Arrange
        Bloco bloco = createBloco();
        bloco.setLaminas(List.of(
            createLamina(1L, CorLamina.AZUL,    PadraoLamina.CASA,      PosicaoLamina.DIREITA),
            createLamina(2L, CorLamina.VERMELHO, PadraoLamina.ESTRELA,   PosicaoLamina.ESQUERDA),
            createLamina(3L, CorLamina.VERDE,    PadraoLamina.NAVIO, PosicaoLamina.FRENTE)
        ));
        Estoque estoque = Estoque.builder().id(1L).build();
        when(estoqueService.findEntityById(1L)).thenReturn(estoque);
        when(blocoRepository.save(any(Bloco.class))).thenReturn(bloco);

        // Act
        Bloco created = blocoService.create(bloco);

        // Assert
        assertNotNull(created);
        verify(blocoRepository, times(1)).save(any(Bloco.class));
        verify(laminaService, times(3)).create(any(Lamina.class));
    }

    @Test
    void deveChamarLaminaServiceParaCadaLaminaAoCriarBlocoComUmaLamina() {
        // Arrange
        Bloco bloco = createBloco();
        Estoque estoque = Estoque.builder().id(1L).build();
        when(estoqueService.findEntityById(1L)).thenReturn(estoque);
        when(blocoRepository.save(any(Bloco.class))).thenReturn(bloco);

        // Act
        blocoService.create(bloco);

        // Assert
        verify(laminaService, times(1)).create(any(Lamina.class));
    }

    @Test
    void deveChamarLaminaServiceParaCadaLaminaAoCriarBlocoComDuasLaminas() {
        // Arrange
        Bloco bloco = createBloco();
        bloco.setLaminas(List.of(
            createLamina(1L, CorLamina.AZUL, PadraoLamina.CASA, PosicaoLamina.DIREITA),
            createLamina(2L, CorLamina.AZUL, PadraoLamina.CASA, PosicaoLamina.ESQUERDA)
        ));
        Estoque estoque = Estoque.builder().id(1L).build();
        when(estoqueService.findEntityById(1L)).thenReturn(estoque);
        when(blocoRepository.save(any(Bloco.class))).thenReturn(bloco);

        // Act
        blocoService.create(bloco);

        // Assert
        verify(laminaService, times(2)).create(any(Lamina.class));
    }

    @Test
    void deveChamarLaminaServiceParaCadaLaminaAoCriarBlocoComTresLaminas() {
        // Arrange
        Bloco bloco = createBloco();
        bloco.setLaminas(List.of(
            createLamina(1L, CorLamina.AZUL, PadraoLamina.CASA, PosicaoLamina.DIREITA),
            createLamina(2L, CorLamina.AZUL, PadraoLamina.CASA, PosicaoLamina.ESQUERDA),
            createLamina(3L, CorLamina.AZUL, PadraoLamina.CASA, PosicaoLamina.FRENTE)
        ));
        Estoque estoque = Estoque.builder().id(1L).build();
        when(estoqueService.findEntityById(1L)).thenReturn(estoque);
        when(blocoRepository.save(any(Bloco.class))).thenReturn(bloco);

        // Act
        blocoService.create(bloco);

        // Assert
        verify(laminaService, times(3)).create(any(Lamina.class));
    }

    @Test
    void deveAtualizarBlocoQuandoIdValido() {
        // Arrange
        Bloco existente = createBloco();
        Bloco atualizado = Bloco.builder()
            .id(1L)
            .cor(CorBloco.VERMELHO)
            .andar(AndarBloco.SEGUNDO)
            .estoque(Estoque.builder().id(2L).build())
            .build();
        when(blocoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(blocoRepository.save(any(Bloco.class))).thenReturn(existente);

        // Act
        Bloco result = blocoService.update(1L, atualizado);

        // Assert
        assertNotNull(result);
        verify(blocoRepository, times(1)).findById(1L);
        verify(blocoRepository, times(1)).save(any(Bloco.class));
    }

    @Test
    void deveRetornarResourceNotFoundExceptionQuandoAtualizarBlocoComIdInvalido() {
        // Arrange
        Long id = 99L;
        Bloco atualizado = createBloco();
        when(blocoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> blocoService.update(id, atualizado));
        verify(blocoRepository, never()).save(any());
    }

    @Test
    void deveAtualizarCamposDoBlocoCorretamente() {
        // Arrange
        Bloco existente = createBloco();
        Bloco novosValores = Bloco.builder()
            .cor(CorBloco.VERMELHO)
            .andar(AndarBloco.SEGUNDO)
            .estoque(Estoque.builder().id(2L).build())
            .build();
        when(blocoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(blocoRepository.save(any(Bloco.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Bloco result = blocoService.update(1L, novosValores);

        // Assert
        assertEquals(CorBloco.VERMELHO, result.getCor());
        assertEquals(AndarBloco.SEGUNDO, result.getAndar());
        assertEquals(2L, result.getEstoque().getId());
    }

    @Test
    void deveDeletarBlocoQuandoIdValido() {
        // Arrange
        when(blocoRepository.existsById(1L)).thenReturn(true);

        // Act
        blocoService.delete(1L);

        // Assert
        verify(blocoRepository, times(1)).existsById(1L);
        verify(blocoRepository, times(1)).deleteById(1L);
    }

    @Test
    void deveRetornarResourceNotFoundExceptionQuandoDeletarBlocoComIdInvalido() {
        // Arrange
        Long id = 99L;
        when(blocoRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> blocoService.delete(id));
        verify(blocoRepository, never()).deleteById(any());
    }

    private Bloco createBloco() {
        return Bloco.builder()
            .id(1L)
            .pedido(null)
            .laminas(
                List.of(
                    createLamina(1L, CorLamina.AZUL, PadraoLamina.CASA, PosicaoLamina.DIREITA)
                )
            )
            .estoque(Estoque.builder().id(1L).build())
            .cor(CorBloco.AZUL)
            .andar(AndarBloco.PRIMEIRO)
            .build();
    }
    private Lamina createLamina(Long id, CorLamina cor, PadraoLamina padrao, PosicaoLamina posicao) {
        return Lamina.builder()
            .id(id)
            .cor(cor)
            .padrao(padrao)
            .posicao(posicao)
            .build();
    }
}