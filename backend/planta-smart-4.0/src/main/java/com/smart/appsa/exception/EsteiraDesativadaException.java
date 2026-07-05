package com.smart.appsa.exception;

import com.smart.appsa.exception.core.BusinessException;

public class EsteiraDesativadaException extends BusinessException {

    public EsteiraDesativadaException() {
        super("Há alguma esteira que não está livre.");
    }

}
