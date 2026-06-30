package com.smart.appsa.dto.clp.stream;

import com.smart.appsa.model.enums.StatusEstacao;

// DTO de stream comum das estações sem dados específicos (PROCESSO e MONTAGEM).
// Por ser um {@code record}, o {@code equals} gerado permite detectar mudança de
// conteúdo entre dois ciclos de leitura (multiplexação só emite quando muda).
public record EstacaoStreamDTO(
        String estacao,
        StatusEstacao status,
        int numeroOP,
        boolean ocupado,
        boolean aguardando,
        boolean manual,
        boolean emergencia,
        boolean recebidoOp,
        boolean startOP,
        boolean finishOP,
        boolean cancelOP,
        int statusBancada) {
}
