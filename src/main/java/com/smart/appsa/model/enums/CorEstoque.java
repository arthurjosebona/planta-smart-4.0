package com.smart.appsa.model.enums;

public enum CorEstoque {
    VAZIO(0),
    PRETO(1),
    VERMELHO(2),
    AZUL(3);

    int value;

    private CorEstoque(int value) {
        this.value = value;
    }

    public int getValue() {return value;};
}
