package com.smart.appsa.exception;

public class InvalidOrdemDeProducaoException extends BusinessException{

    public InvalidOrdemDeProducaoException(int op) {
        super(String.format(
            "Ordem de produção inválida: %d",
            op
        ));
    }

}
