package com.smart.appsa.unitario.service;

import com.smart.appsa.service.EstoqueService;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smart.appsa.dto.response.EstoqueResponseDTO;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.repository.EstoqueRepository;

@ExtendWith(MockitoExtension.class)
public class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private EstoqueService estoqueService;

    // â”€â”€â”€ CRUD â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    public void deveRetornarTodoOEstoque() {
        // Arrange
        Estoque e1 = createEstoque(1L, 1, CorEstoque.AZUL);
        Estoque e2 = createEstoque(2L, 2, CorEstoque.VAZIO);
        when(estoqueRepository.findAll()).thenReturn(List.of(e1, e2));

        // Act
        List<EstoqueResponseDTO> result = estoqueService.findAll();

        // Assert
        assertEquals(2, result.size());
        verify(estoqueRepository, times(1)).findAll();
    }

    @Test
    public void deveRetornarEstoquePorId() {
        // Arrange
        Estoque estoque = createEstoque(1L, 1, CorEstoque.AZUL);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        // Act
        EstoqueResponseDTO result = estoqueService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    public void deveRetornarEntityPorId() {
        // Arrange
        Estoque estoque = createEstoque(1L, 1, CorEstoque.AZUL);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));

        // Act
        Estoque result = estoqueService.findEntityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(CorEstoque.AZUL, result.getCorEstoque());
    }

    @Test
    public void deveRetornarEstoqueDisponivel() {
        // Arrange
        Estoque e1 = createEstoque(1L, 1, CorEstoque.AZUL);
        Estoque e2 = createEstoque(2L, 2, CorEstoque.VERMELHO);
        when(estoqueRepository.findByCorEstoqueNot(CorEstoque.VAZIO)).thenReturn(List.of(e1, e2));

        // Act
        List<EstoqueResponseDTO> result = estoqueService.findAvailable();

        // Assert
        assertEquals(2, result.size());
        verify(estoqueRepository, times(1)).findByCorEstoqueNot(CorEstoque.VAZIO);
    }

    @Test
    public void deveRetornarEstoqueIndisponivel() {
        // Arrange
        Estoque e1 = createEstoque(3L, 3, CorEstoque.VAZIO);
        when(estoqueRepository.findByCorEstoque(CorEstoque.VAZIO)).thenReturn(List.of(e1));

        // Act
        List<EstoqueResponseDTO> result = estoqueService.findUnavailable();

        // Assert
        assertEquals(1, result.size());
        verify(estoqueRepository, times(1)).findByCorEstoque(CorEstoque.VAZIO);
    }

    @Test
    public void deveAtribuirCorAoBloco() {
        // Arrange
        Estoque estoque = createEstoque(1L, 1, CorEstoque.VAZIO);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.save(any(Estoque.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Estoque result = estoqueService.assignBlockColor(1L, CorEstoque.AZUL);

        // Assert
        assertEquals(CorEstoque.AZUL, result.getCorEstoque());
        verify(estoqueRepository, times(1)).save(any(Estoque.class));
    }

    @Test
    public void deveContarEstoquePorCor() {
        // Arrange
        when(estoqueRepository.countByCorEstoque(CorEstoque.AZUL)).thenReturn(3L);

        // Act
        Long result = estoqueService.countByCorEstoque(CorEstoque.AZUL);

        // Assert
        assertEquals(3L, result);
        verify(estoqueRepository, times(1)).countByCorEstoque(CorEstoque.AZUL);
    }

    // â”€â”€â”€ FALHA â€” CAMPOS FALTANDO â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    public void deveLancarExcecaoAoBuscarIdInexistente() {
        // Arrange
        when(estoqueRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> estoqueService.findById(99L));
    }

    @Test
    public void deveLancarExcecaoAoBuscarEntityIdInexistente() {
        // Arrange
        when(estoqueRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> estoqueService.findEntityById(99L));
    }

    // â”€â”€â”€ FALHA â€” REGRAS DE NEGÃ“CIO â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    public void deveLancarExcecaoAoAtribuirCorEmIdInexistente() {
        // Arrange
        when(estoqueRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> estoqueService.assignBlockColor(99L, CorEstoque.AZUL));
    }

    // â”€â”€â”€ HELPER â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private Estoque createEstoque(Long id, int posicaoFisica, CorEstoque cor) {
        return Estoque.builder()
                .id(id)
                .posicaoFisica(posicaoFisica)
                .corEstoque(cor)
                .build();
    }
}