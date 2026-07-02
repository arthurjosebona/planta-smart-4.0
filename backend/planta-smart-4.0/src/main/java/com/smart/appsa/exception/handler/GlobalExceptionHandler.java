package com.smart.appsa.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smart.appsa.dto.response.ErrorResponseDTO;
import com.smart.appsa.exception.core.AppException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponseDTO> handleAppException(AppException ex) {
        log.error("AppException [HTTP {}]: {}", ex.getStatus().value(), ex.getMessage(), ex);
        return ResponseEntity
            .status(ex.getStatus())
            .body(new ErrorResponseDTO(ex.getMessage(), ex.getStatus().value()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponseDTO(ex.getMessage(), 400));
    }

}