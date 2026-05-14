package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CorLamina {
    VERMELHO(1),
    AZUL(2),
    AMARELO(3),
    VERDE(4),
    PRETO(5),
    BRANCO(6);

    Integer value;

    @JsonCreator
    public static CorLamina fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (CorLamina c : values()) {
            if (parsed.equals(c.value)) return c;
        }
        throw new IllegalArgumentException("CorLamina inválida: " + value);
    }

    private CorLamina(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}
