package com.smart.appsa.exception;

import java.util.List;

import com.smart.appsa.exception.core.BusinessException;
import com.smart.appsa.model.enums.PosicaoLamina;

public class DuplicatePosicaoException extends BusinessException {

    public DuplicatePosicaoException(List<PosicaoLamina> posicoesDuplicadas) {
        super(String.format(
            "Não é permitido blocos com posicoes de laminas duplicadas: %s" ,
            posicoesDuplicadas.stream().map(PosicaoLamina::name).toList()
        ));
    }
    
}
