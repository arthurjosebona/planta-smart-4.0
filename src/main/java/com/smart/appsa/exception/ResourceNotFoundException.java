package com.smart.appsa.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException{

    public ResourceNotFoundException(String resource, Long id) {
        super(
            String.format("%s não encontrado com ID: %l", resource, id), 
            HttpStatus.NOT_FOUND
        );
    }

    public ResourceNotFoundException(String resource, String fieldName, Object value) {
        super(
            String.format("%s não encontrado com %s: %s", resource, fieldName, value), 
            HttpStatus.NOT_FOUND
        );
    }

}
