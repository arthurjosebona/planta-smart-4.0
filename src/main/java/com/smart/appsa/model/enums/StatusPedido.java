package com.smart.appsa.model.enums;

public enum StatusPedido {
    PENDENTE(1),
    PRODUCAO(2),
    CONCLUIDO(3);

    int value;

    private StatusPedido(int value) {
        this.value = value;
    }

    public int getValue() {return value;};
}

