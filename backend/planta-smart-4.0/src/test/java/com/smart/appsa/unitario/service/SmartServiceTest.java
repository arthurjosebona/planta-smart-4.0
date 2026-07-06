package com.smart.appsa.unitario.service;

import com.smart.appsa.service.SmartService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.dto.clp.PedidoConfigDTO;
import com.smart.appsa.dto.clp.PedidoInfoDTO;
import com.smart.appsa.exception.ClpComunicacaoException;
import com.smart.appsa.exception.EsteiraDesativadaException;
import com.smart.appsa.model.clp.MontagemInfo;

@ExtendWith(MockitoExtension.class)
public class SmartServiceTest {

    @Mock
    private PlcConnectionService plcConnectionService;
    @Mock
    private AppStateConfig appStateConfig;
    @Mock
    private MontagemInfo montagemInfo;
    @Mock
    private PlcConnector plcConnector;

    @InjectMocks
    private SmartService smartService;

    // â”€â”€â”€ enviarParaProducao â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Test
    void deveLancarEsteiraDesativadaExceptionQuandoSupervisorioNaoEstiverLivre() {
        // Estoque fora de "LIVRE" jÃ¡ barra o envio (short-circuit no primeiro check).
        when(montagemInfo.getSupervisorioEstoque()).thenReturn("DESLIGADO");

        assertThrows(EsteiraDesativadaException.class,
            () -> smartService.enviarParaProducao(createConfig(), createDetalhes()));
        verify(plcConnectionService, never()).getConnection(any());
    }

    @Test
    void deveLancarClpComunicacaoExceptionQuandoConexaoIndisponivel() {
        // Todas as esteiras livres, mas o CLP nÃ£o responde (conexÃ£o nula).
        when(montagemInfo.getSupervisorioEstoque()).thenReturn("LIVRE");
        when(montagemInfo.getSupervisorioExpedicao()).thenReturn("LIVRE");
        when(montagemInfo.getSupervisorioMontagem()).thenReturn("LIVRE");
        when(montagemInfo.getSupervisorioProcesso()).thenReturn("LIVRE");
        when(plcConnectionService.getConnection("192.168.0.10")).thenReturn(null);

        assertThrows(ClpComunicacaoException.class,
            () -> smartService.enviarParaProducao(createConfig(), createDetalhes()));
    }

    private PedidoConfigDTO createConfig() {
        return PedidoConfigDTO.builder()
            .idPedido(1L)
            .tipoPedido(1)
            .tampaPedido(2)
            .ipClp("192.168.0.10")
            .build();
    }

    private PedidoInfoDTO createDetalhes() {
        return PedidoInfoDTO.builder()
            .numeroPedido(1)
            .andares(1)
            .posicaoExpedicao(1)
            .build();
    }
}