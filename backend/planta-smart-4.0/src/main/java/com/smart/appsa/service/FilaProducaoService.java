package com.smart.appsa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.springframework.stereotype.Component;

import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.exception.PedidoNaoPendenteException;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilaProducaoService {

    private final LinkedBlockingQueue<Long> pedidos = new LinkedBlockingQueue<>();
    // Starts at 0: worker blocks after dispatching, released when an order completes.
    private final Semaphore ordemConcluida = new Semaphore(0);

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;

    @PostConstruct
    void start() {
        Thread worker = new Thread(this::loop, "fila-producao-worker");
        worker.setDaemon(true);
        worker.start();
    }

    private void loop() {
        // If the app restarted with an order already in PRODUCAO, wait for its
        // completion signal before touching the queue so we never run two orders at once.
        Optional<Pedido> emProducao = pedidoRepository.findFirstByStatus(StatusPedido.PRODUCAO);
        if (emProducao.isPresent()) {
            log.warn("Pedido {} em PRODUCAO após (re)start — aguardando conclusão.", emProducao.get().getId());
            try {
                ordemConcluida.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Long id = pedidos.take(); // sleeps here until a new order arrives

                log.info("Despachando pedido {} para produção.", id);
                try {
                    pedidoService.startProduction(id);
                } catch (Exception e) {
                    log.error("Falha ao iniciar pedido {}: {} — removido da fila.", id, e.getMessage());
                    continue;
                }

                log.info("Pedido {} em produção. Thread aguardando conclusão...", id);
                ordemConcluida.acquire(); // sleeps until notifyOrderCompleted() is called
                log.info("Pedido {} concluído. Pedidos restantes na fila: {}.", id, pedidos.size());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Enqueues an order for sequential production dispatch.
     * Validates that the order is PENDENTE before adding.
     */
    public PedidoResponseDTO enqueueOrder(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new PedidoNaoPendenteException("enfileirar", pedido.getStatus());
        }
        pedidos.add(id);
        log.info("Pedido {} enfileirado. Posição na fila: {}.", id, pedidos.size());
        return PedidoMapper.mapDto(pedido);
    }

    /** Called by ExpedicaoComm when the CLP confirms the order was stored. */
    public void notifyOrderCompleted() {
        ordemConcluida.release();
    }

    /** Snapshot of queued order IDs in dispatch order (head = next to run). */
    public List<Long> filaAtual() {
        return new ArrayList<>(pedidos);
    }
}
