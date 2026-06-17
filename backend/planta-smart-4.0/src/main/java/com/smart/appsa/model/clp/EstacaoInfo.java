package com.smart.appsa.model.clp;

import com.smart.appsa.model.enums.StatusEstacao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EstacaoInfo {
    private boolean recebidoOp;
    private int numeroOP;

    private boolean finishOP;
    private boolean startOP;
    private boolean cancelOP;

    private boolean manual;
    private boolean emergencia;
    private boolean ocupado;
    private boolean aguardando;

    public StatusEstacao getStatus() {
        if (ocupado) return StatusEstacao.OCUPADO;
        if (manual) return StatusEstacao.MANUAL;
        if (emergencia) return StatusEstacao.EMERGENCIA;
        return StatusEstacao.AGUARDANDO;
    }
}
