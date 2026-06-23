package com.smart.appsa.dto.clp.stream;

import java.util.List;

import com.smart.appsa.model.enums.StatusEstacao;

// DTO de stream da estação EXPEDICAO.
public record ExpedicaoStreamDTO(
        String estacao,
        StatusEstacao status,
        int numeroOP,
        boolean ocupado,
        boolean aguardando,
        boolean manual,
        boolean emergencia,
        boolean pedirPosicaoExp,
        boolean adicionarExpedicao,
        boolean removerExpedicao,
        boolean iniciarGuardarExp,
        boolean recebidoExpedicao,
        int posicaoGuardarExp,
        int posicaoGuardadoExpedicao,
        int posicaoRemovidoExpedicao,
        int opGuardadoExpedicao,
        List<Integer> orderExpedicao,
        int statusExpedicao) {
}
