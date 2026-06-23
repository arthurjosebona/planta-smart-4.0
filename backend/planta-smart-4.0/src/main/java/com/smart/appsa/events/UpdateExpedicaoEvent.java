package com.smart.appsa.events;

import org.springframework.context.ApplicationEvent;

public class UpdateExpedicaoEvent extends ApplicationEvent {
    private final int posicao;
    private final int codPedido;

    public UpdateExpedicaoEvent(Object source, int posicao, int codPedido) {
        super(source);
        this.posicao = posicao;
        this.codPedido = codPedido;
    }

    public int getPosicao()   { return posicao; }
    public int getCodPedido() { return codPedido; }
}
