package com.smart.appsa.model.clp;

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
    private boolean ocupado;
    private boolean aguardando;
    private boolean manual;
    private boolean emergencia;
}
