package com.smart.appsa.exception;

import com.smart.appsa.exception.core.BusinessException;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.enums.CorBloco;

public class CorIncompatibleWithEstoqueException extends BusinessException{

    public CorIncompatibleWithEstoqueException(CorBloco cor, Estoque estoque) {
        super(String.format(
            "A cor %s é incompatível com o estoque de posição %d com cor %s", 
            cor, estoque.getPosicaoFisica(), estoque.getCorEstoque().name()
        ));
    }

}
