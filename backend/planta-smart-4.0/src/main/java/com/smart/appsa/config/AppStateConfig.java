package com.smart.appsa.config;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class AppStateConfig {
    // Permite visualização entre diferentes threads
    private volatile boolean readOnly;
    private volatile boolean pedidoEmCurso;
    private volatile byte statusEstoque;
    private volatile byte statusProducao;
    private volatile byte statusExpedicao;
    private volatile byte statusProcesso;
    private volatile byte statusMontagem;
    private volatile boolean blockFinished;
    private volatile boolean aux_expedicao;
    private volatile int posicaoExpedicaoSolicitada;

    // Sempre em UTC (ver EstoqueComm#confirmarInicioPedido). O frontend precisa
    // interpretar essa string como UTC, já que LocalDateTime não carrega zona.
    private volatile LocalDateTime registroInicioPedido;

    public void resetarStatus() {
        statusEstoque = 0;
        statusExpedicao = 0;
        statusProcesso = 0;
        statusMontagem = 0;
    }
}
