package com.smart.appsa.service;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.smart.appsa.dto.response.FilaStreamDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.events.FilaAlteradaEvent;
import com.smart.appsa.events.PedidoConcluidoEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Fila FIFO de pedidos para produção, mantida apenas em memória.
//
// <p>Garante que exista no máximo um pedido em execução a qualquer instante: ao
// enfileirar, se nada está em execução, o pedido é iniciado imediatamente; caso
// contrário fica pendente. Quando a EXPEDIÇÃO conclui o pedido em curso (via
// {@link PedidoConcluidoEvent}), o próximo da fila é iniciado automaticamente.
//
// <p>A detecção de conclusão é consumida de forma assíncrona ({@code @Async} no
// executor já existente) para que as escritas TCP do start nunca rodem na thread
// de leitura do PLC. A transição de {@code emExecucao} é protegida por lock.
@Slf4j
@Service
@RequiredArgsConstructor
public class FilaProducaoService {

    private final PedidoService pedidoService;
    private final CronometroProducaoService cronometroProducaoService;
    private final ApplicationEventPublisher eventPublisher;

    // fila thread-safe só com os IDs que estão esperando, não tem o atual
    private final Queue<Long> pendentes = new ConcurrentLinkedQueue<>();
    private volatile Long emExecucao;
    private final ReentrantLock lock = new ReentrantLock();

    // Enfileira um pedido. Se não há nada em execução, promove e inicia imediatamente;
    // senão adiciona ao fim da fila. Valida a existência do pedido antes (lança
    // ResourceNotFoundException, tratada pelo GlobalExceptionHandler).
    public void enfileirar(Long pedidoId) {
        pedidoService.findById(pedidoId); // Valida que existe

        boolean iniciarAgora = false;
        lock.lock();
        try {
            if (emExecucao == null) {
                emExecucao = pedidoId;
                iniciarAgora = true;
                log.info("Pedido {} promovido para execução imediata.", pedidoId);
            } else {
                pendentes.add(pedidoId);
                log.info("Pedido {} enfileirado. Fila atual: {} pendente(s), em execução: {}",
                        pedidoId, pendentes.size(), emExecucao);
            }
        } finally {
            lock.unlock();
        }

        if (iniciarAgora) {
            iniciar(pedidoId);
        }
        eventPublisher.publishEvent(new FilaAlteradaEvent(this));
    }

    // Reage à conclusão do pedido em curso: retira-o de execução e inicia o próximo
    // pendente (se houver). Executa em thread própria (executor de escrita da expedição),
    // nunca na thread de leitura do PLC.
    @Async("plcWriteExpedicaoExecutor")
    @EventListener
    public void onPedidoConcluido(PedidoConcluidoEvent event) {
        log.info("PedidoConcluido recebido (OP {}). Avançando fila...", event.getOp());
        Long proximo;
        lock.lock();
        try {
            emExecucao = pendentes.poll(); 
            proximo = emExecucao;
        } finally {
            lock.unlock();
        }

        if (proximo != null) {
            log.info("Próximo pedido da fila: {}. Iniciando produção.", proximo);
            iniciar(proximo);
        } else {
            log.info("Fila vazia após conclusão do pedido. Aguardando novo enfileiramento.");
        }
        eventPublisher.publishEvent(new FilaAlteradaEvent(this));
    }

    private void iniciar(Long pedidoId) {
        log.info("Iniciando produção do pedido {}.", pedidoId);
        try {
            pedidoService.startProduction(pedidoId);
        } catch (Exception e) {
            log.error("Erro ao iniciar produção do pedido {}: {}", pedidoId, e.getMessage(), e);
        }
    }

    // Snapshot imutável da fila para REST/SSE.
    public FilaStreamDTO snapshot() {
        Long emExecId = emExecucao;
        PedidoResponseDTO emExec = emExecId == null ? null : safeFind(emExecId);
        List<PedidoResponseDTO> pend = pendentes.stream()
                .map(this::safeFind)
                .filter(Objects::nonNull)
                .toList();
        long tempo = emExecId == null ? 0 : cronometroProducaoService.getTempoExecucaoSegundos();
        return new FilaStreamDTO(emExec, tempo, pend);
    }

    private PedidoResponseDTO safeFind(Long id) {
        try {
            return pedidoService.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public Long getEmExecucao() {
        return emExecucao;
    }

    public List<Long> getPendentes() {
        return List.copyOf(pendentes);
    }
}
