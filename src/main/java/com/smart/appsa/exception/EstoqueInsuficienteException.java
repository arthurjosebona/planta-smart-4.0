package com.smart.appsa.exception;

public class EstoqueInsuficienteException extends BusinessException {

    public EstoqueInsuficienteException(String cor, long necessario, long disponivel) {
        super(String.format(
            "Estoque insuficiente para cor %s. Necessário: %d, Disponível: %d", 
            cor, necessario, disponivel
        ));
    }

}
