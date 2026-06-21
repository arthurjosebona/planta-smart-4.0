package com.smart.appsa.exception;

import org.springframework.http.HttpStatus;

import com.smart.appsa.exception.core.AppException;

public class ClpComunicacaoException extends AppException {
    public ClpComunicacaoException(String ipClp, String motivo) {
        super("Falha na comunicação com o CLP " + ipClp + ": " + motivo, HttpStatus.valueOf(502));
    }

    public ClpComunicacaoException(String ipClp, Throwable causa) {
        super("Falha na comunicação com o CLP " + ipClp + ": " + causa.getMessage(), HttpStatus.valueOf(502));
    }
}
