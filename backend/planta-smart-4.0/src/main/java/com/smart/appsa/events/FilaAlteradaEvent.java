package com.smart.appsa.events;

import org.springframework.context.ApplicationEvent;

// Sinaliza que o estado da fila de produção (pedido em execução, pendentes ou tempo
// de execução) mudou. O {@code SseService} escuta e reemite o snapshot da fila — só
// quando o conteúdo realmente difere do último emitido.
public class FilaAlteradaEvent extends ApplicationEvent {
    public FilaAlteradaEvent(Object source) {
        super(source);
    }
}
