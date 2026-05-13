package com.smart.appsa.model.enums;

public enum TipoPedido {
    SIMPLES(1),
    DUPLO(2),
    TRIPLO(3);

    int value;

    private TipoPedido(int value) {
        this.value = value;
    }

    public int getValue() {return value;};
}
