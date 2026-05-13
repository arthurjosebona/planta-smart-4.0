package com.smart.appsa.model.enums;

public enum CorTampa {
    PRETO(1),
    VERMELHO(2),
    AZUL(3);

    int value;

    private CorTampa(int value) {
        this.value = value;
    }

    public int getValue() {return value;};
}

