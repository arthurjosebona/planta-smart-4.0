package com.smart.appsa.exception;

public class PosicaoExpedicaoOcupadaException extends BusinessException {

    public PosicaoExpedicaoOcupadaException(int posicaoFisica) {
        super(String.format(
            "Posição física %d da expedição já foi ocupada.",
            posicaoFisica
        ));
    }
    
}
