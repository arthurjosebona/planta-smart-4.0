package com.smart.appsa.events;

import org.springframework.context.ApplicationEvent;

// Publicado quando a EXPEDIÇÃO confirma que o bloco do pedido em curso foi guardado
// (conclusão real do pedido). Consumido pela fila de produção para iniciar o próximo.
public class PedidoConcluidoEvent extends ApplicationEvent {
    private final int op;

    public PedidoConcluidoEvent(Object source, int op) {
        super(source);
        this.op = op;
    }

    public int getOp() {
        return op;
    }
}
