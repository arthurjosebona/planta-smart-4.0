package com.smart.appsa.exception;

import com.smart.appsa.exception.core.BusinessException;

public class ExpedicaoLotadaException extends BusinessException {

    public ExpedicaoLotadaException() {
        super("Todas as 12 posições da expedição já estão lotadas.");
    }
    
}
