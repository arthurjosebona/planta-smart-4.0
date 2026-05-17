package com.smart.appsa.exception;

import com.smart.appsa.model.enums.PosicaoLamina;

public class LaminaPosicaoOcupadaException extends BusinessException {

    public LaminaPosicaoOcupadaException(PosicaoLamina posicaoLamina) {
        super(String.format(
            "Já existe uma lâmina na posição %s neste bloco.", 
            posicaoLamina.name()
        ));
    }

}
