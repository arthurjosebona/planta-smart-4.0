package com.smart.appsa.model.enums;

public enum CorLamina {
    VERMELHO(1),
    AZUL(2),
    AMARELO(3),
    VERDE(4),
    PRETO(5),
    BRANCO(6);

    int value;

    private CorLamina(int value) {
        this.value = value;
    }

    public int getValue() {return value;};
}
