package com.smart.appsa.events;

import org.springframework.context.ApplicationEvent;

// Publicado nos mesmos pontos em que {@code AppStateConfig.pedidoEmCurso} muda:
// {@code true} ao ESTOQUE confirmar o início do pedido, {@code false} ao a EXPEDIÇÃO
// guardar o bloco. Inicia/para o cronômetro do pedido em execução.
public class PedidoEmCursoEvent extends ApplicationEvent {
    private final boolean emCurso;

    public PedidoEmCursoEvent(Object source, boolean emCurso) {
        super(source);
        this.emCurso = emCurso;
    }

    public boolean isEmCurso() {
        return emCurso;
    }
}
