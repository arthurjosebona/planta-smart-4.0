package com.smart.appsa.exception;

import com.smart.appsa.model.enums.TipoPedido;

public class TipoIncompativelComBlocosException extends BusinessException{

    public TipoIncompativelComBlocosException(TipoPedido tipoPedido, int qtdBlocos) {
        super(String.format(
            "Tipo do pedido incompatível com a quantidade de blocos: %s - %d", 
            tipoPedido.name(), qtdBlocos
        ));
    }

}
