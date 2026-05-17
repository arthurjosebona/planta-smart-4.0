package com.smart.appsa.exception;

public class InvalidPosicaoExpedicaoException extends BusinessException {

    public InvalidPosicaoExpedicaoException(int posicaoExpedicao) {
        super(String.format(
            "Posição inválida: %d. A expedição tem 12 posições (1 a 12).", 
            posicaoExpedicao
        ));
    }

}
