package com.smart.appsa.exception;

import com.smart.appsa.exception.core.BusinessException;
import com.smart.appsa.model.enums.CorEstoque;

public class EstoqueInsuficienteException extends BusinessException {

    public EstoqueInsuficienteException(String cor) {
        super(String.format(
            "Estoque insuficiente para cor %s.", 
            cor
        ));
    }

    public EstoqueInsuficienteException(CorEstoque cor) {
        super(String.format(
            "Estoque insuficiente para cor %s.", 
            cor.name()
        ));
    }

}
