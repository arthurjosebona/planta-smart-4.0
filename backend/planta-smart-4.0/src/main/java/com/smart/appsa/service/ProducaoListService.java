package com.smart.appsa.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.smart.appsa.model.Pedido;
import com.smart.appsa.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProducaoListService {
    private final ConcurrentLinkedQueue<Long> pedidos = new ConcurrentLinkedQueue<>();
    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;

    @Scheduled(fixedDelayString = "${delay.order.queue:1000}")
    public void processOrder() {
        log.debug("Fila de pedidos: {}", pedidos);

        Long head = pedidos.peek();
        if (head == null) {
            return;
        }

        Pedido pedido = pedidoRepository.findById(head).orElse(null);
        if (pedido == null) {
            // Pedido enfileirado foi deletado: descarta o id para não travar a fila.
            log.warn("Pedido {} não existe mais — removido da fila.", head);
            pedidos.poll();
            return;
        }

        switch (pedido.getStatus()) {
            case CONCLUIDO -> pedidos.poll(); // já terminou → avança
            case PENDENTE -> pedidoService.startProduction(head); // dispara → vira PRODUCAO
            default -> {
                /* PRODUCAO não precisa de tratamento pois está rodando */ }
        }
    }

    public void addOrder(Long id) {
        pedidos.add(id);
    }

    public List<Long> filaAtual() {
        return new ArrayList<>(pedidos);
    }
}