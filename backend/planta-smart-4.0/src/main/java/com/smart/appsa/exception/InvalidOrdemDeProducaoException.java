package com.smart.appsa.exception;

import com.smart.appsa.exception.core.BusinessException;

public class InvalidOrdemDeProducaoException extends BusinessException{

    public InvalidOrdemDeProducaoException(int op) {
        super(String.format(
            "Ordem de produção inválida: %d",
            op
        ));
    }

}
