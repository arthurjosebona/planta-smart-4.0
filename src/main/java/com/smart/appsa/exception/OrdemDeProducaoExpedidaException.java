package com.smart.appsa.exception;

public class OrdemDeProducaoExpedidaException extends BusinessException {

    public OrdemDeProducaoExpedidaException(int ordemDeProducao) {
        super(String.format(
            "Ordem de produção %d já foi expedida", 
            ordemDeProducao
        ));
    }

}
