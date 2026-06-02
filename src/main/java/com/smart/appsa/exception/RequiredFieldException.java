package com.smart.appsa.exception;

import com.smart.appsa.exception.core.BusinessException;

public class RequiredFieldException extends BusinessException {

    public RequiredFieldException(String field) {
        super(String.format(
            "O campo %s é obrigatório.", 
            field
        ));
    }

}
