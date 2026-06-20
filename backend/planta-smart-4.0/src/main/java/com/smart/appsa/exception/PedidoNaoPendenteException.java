package com.smart.appsa.exception;

import com.smart.appsa.exception.core.BusinessException;
import com.smart.appsa.model.enums.StatusPedido;

public class PedidoNaoPendenteException extends BusinessException {

    public PedidoNaoPendenteException(String operacao, StatusPedido status) {
        super(String.format(
            "Operação '%s' só é permitida para pedidos PENDENTE. Status atual: %s",
            operacao,
            status
        ));
    }

}
