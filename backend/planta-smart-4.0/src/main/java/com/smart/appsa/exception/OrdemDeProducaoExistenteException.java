package com.smart.appsa.exception;

import com.smart.appsa.exception.core.BusinessException;

public class OrdemDeProducaoExistenteException extends BusinessException {
    public OrdemDeProducaoExistenteException(Integer ordemDeProducao) {
        super(
            String.format(
                "Já existem um pedido com a ordem de produção %d", 
                ordemDeProducao
            )
        );
            
    }
}
