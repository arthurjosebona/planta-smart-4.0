package com.smart.appsa.exception;

public class RequiredFieldException extends BusinessException {

    public RequiredFieldException(String field) {
        super(String.format(
            "O campo %s é obrigatório.", 
            field
        ));
    }

}
