package com.smart.appsa.config;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    public void setStatusEstoque(byte valor) {
        if (this.statusEstoque != valor) {
            log.info("STATUS BANCADA: statusEstoque {} -> {}", this.statusEstoque, valor);
        }
        this.statusEstoque = valor;
    }

    public void setStatusProducao(byte valor) {
        if (this.statusProducao != valor) {
            log.info("STATUS BANCADA: statusProducao {} -> {}", this.statusProducao, valor);
        }
        this.statusProducao = valor;
    }

    public void setStatusExpedicao(byte valor) {
        if (this.statusExpedicao != valor) {
            log.info("STATUS BANCADA: statusExpedicao {} -> {}", this.statusExpedicao, valor);
        }
        this.statusExpedicao = valor;
    }

    public void setStatusProcesso(byte valor) {
        if (this.statusProcesso != valor) {
            log.info("STATUS BANCADA: statusProcesso {} -> {}", this.statusProcesso, valor);
        }
        this.statusProcesso = valor;
    }

    public void setStatusMontagem(byte valor) {
        if (this.statusMontagem != valor) {
            log.info("STATUS BANCADA: statusMontagem {} -> {}", this.statusMontagem, valor);
        }
        this.statusMontagem = valor;
    }

    public void resetarStatus() {
        log.info("STATUS BANCADA: resetando todos os status para 0 (eram: estoque={}, processo={}, montagem={}, expedicao={})",
                statusEstoque, statusProcesso, statusMontagem, statusExpedicao);
        statusEstoque = 0;
        statusExpedicao = 0;
        statusProcesso = 0;
        statusMontagem = 0;
    }
}
