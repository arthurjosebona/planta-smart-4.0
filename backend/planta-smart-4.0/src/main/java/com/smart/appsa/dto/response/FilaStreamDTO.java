package com.smart.appsa.dto.response;

import java.util.List;

// Snapshot da fila de produção exposto via REST e SSE.
// {@code emExecucao} é o pedido sendo produzido (ou {@code null}); {@code pendentes}
// são os enfileirados em ordem FIFO; {@code tempoExecucaoSegundos} é o tempo decorrido
// do pedido em execução (0 quando não há pedido em curso).
public record FilaStreamDTO(
        PedidoResponseDTO emExecucao,
        long tempoExecucaoSegundos,
        List<PedidoResponseDTO> pendentes) {
}
