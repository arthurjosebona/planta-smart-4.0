package com.smart.appsa.model.enums;

public enum PosicaoLamina {
    ESQUERDA(1),
    FRENTE(2),
    DIREITA(3);
    int value;

    private PosicaoLamina(int value) {
        this.value = value;
    }

    public int getValue() {return value;};
}
