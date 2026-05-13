package com.smart.appsa.model.enums;

public enum PadraoLamina {
    NENHUM(0),
    CASA(1),
    NAVIO(2),
    ESTRELA(3);

    int value;

    private PadraoLamina(int value) {
        this.value = value;
    }

    public int getValue() {return value;};
}
