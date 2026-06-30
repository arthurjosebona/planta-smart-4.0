package com.smart.appsa.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.smart.appsa.events.FilaAlteradaEvent;
import com.smart.appsa.events.PedidoEmCursoEvent;

import lombok.RequiredArgsConstructor;

// Cronômetro do pedido em execução. Dirigido por evento: inicia ao receber
// {@link PedidoEmCursoEvent} com {@code emCurso=true} e para/zera quando {@code false}.
// Um tick de 1s recalcula o tempo decorrido e dispara a reemissão do estado da fila
// via {@link FilaAlteradaEvent}. O listener apenas grava/limpa o instante de início
// (operação trivial), nunca bloqueando a thread de leitura do PLC.
@Service
@RequiredArgsConstructor
public class CronometroProducaoService {

    private final ApplicationEventPublisher eventPublisher;

    // Instante de início do pedido em execução; {@code null} quando não há pedido em curso.
    private volatile Instant inicio;
    private volatile long tempoExecucaoSegundos;

    @EventListener
    public void onPedidoEmCurso(PedidoEmCursoEvent event) {
        if (event.isEmCurso()) {
            inicio = Instant.now();
            tempoExecucaoSegundos = 0;
        } else {
            inicio = null;
            tempoExecucaoSegundos = 0;
        }
    }

    // Tick periódico (roda no scheduler do Spring, fora da thread de leitura do PLC).
    // Só recalcula e reemite enquanto há pedido em execução.
    @Scheduled(fixedRate = 1000)
    public void tick() {
        Instant ini = inicio;
        if (ini == null) {
            return;
        }
        tempoExecucaoSegundos = Duration.between(ini, Instant.now()).toSeconds();
        eventPublisher.publishEvent(new FilaAlteradaEvent(this));
    }

    public long getTempoExecucaoSegundos() {
        return tempoExecucaoSegundos;
    }
}
