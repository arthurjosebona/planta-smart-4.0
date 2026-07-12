package com.smart.appsa.dto.clp.stream;

import com.smart.appsa.model.enums.StatusEstacao;

public record ProcessoStreamDTO(
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
