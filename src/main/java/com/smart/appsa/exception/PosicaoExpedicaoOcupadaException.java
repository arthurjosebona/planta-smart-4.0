package com.smart.appsa.exception;

import com.smart.appsa.exception.core.BusinessException;

public class PosicaoExpedicaoOcupadaException extends BusinessException {

    public PosicaoExpedicaoOcupadaException(int posicaoFisica) {
        super(String.format(
            "Posição física %d da expedição já foi ocupada.",
            posicaoFisica
        ));
    }
    
}
