package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PosicaoLamina {
    ESQUERDA(1),
    FRENTE(2),
    DIREITA(3);

    Integer value;

    @JsonCreator
    public static PosicaoLamina fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (PosicaoLamina p : values()) {
            if (parsed.equals(p.value)) return p;
        }
        throw new IllegalArgumentException("PosicaoLamina inválida: " + value);
    }

    private PosicaoLamina(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}
