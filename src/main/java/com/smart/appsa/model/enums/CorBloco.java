package com.smart.appsa.model.enums;

public enum CorBloco {
    PRETO(1),
    VERMELHO(2),
    AZUL(3);

    int value;

    private CorBloco(int value) {
        this.value = value;
    }

    public int getValue() {return value;};
}
