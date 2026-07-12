package com.smart.appsa.dto.clp.stream;

import com.smart.appsa.model.enums.StatusEstacao;

import lombok.Builder;

@Builder
public record MontagemStreamDTO(
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
    int statusBancada,
    String supervisorioEstoque,
    String supervisorioProcesso,
    String supervisorioMontagem,
    String supervisorioExpedicao
) {}
