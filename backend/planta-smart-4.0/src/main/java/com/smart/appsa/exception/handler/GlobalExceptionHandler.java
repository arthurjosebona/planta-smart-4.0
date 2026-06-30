package com.smart.appsa.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smart.appsa.dto.response.ErrorResponseDTO;
import com.smart.appsa.exception.core.AppException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponseDTO> handleAppException(AppException ex) {
        System.out.println("STACK TRACE ERRO: ");
        ex.printStackTrace();
        System.out.println(ex.getMessage() + " " + String.valueOf(ex.getStatus().value()));
        return ResponseEntity
            .status(ex.getStatus())
            .body(new ErrorResponseDTO(ex.getMessage(), ex.getStatus().value()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        System.out.println(ex.getMessage());
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponseDTO(ex.getMessage(), 400));
    }
    
}