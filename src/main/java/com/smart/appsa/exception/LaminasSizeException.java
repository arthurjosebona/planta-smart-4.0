package com.smart.appsa.exception;

public class LaminasSizeException extends BusinessException {

    public LaminasSizeException(int qtdLaminas) {
        super(String.format(
            "Número de lâminas %d inválido. O valor deve estar entre 0-3.", 
            qtdLaminas
        ));
    }

}
