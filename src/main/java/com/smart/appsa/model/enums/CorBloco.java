package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CorBloco {
    PRETO(1),
    VERMELHO(2),
    AZUL(3);

    Integer value;

    @JsonCreator
    public static CorBloco fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (CorBloco c : values()) {
            if (parsed.equals(c.value)) return c;
        }
        throw new IllegalArgumentException("CorBloco inválida: " + value);
    }

    private CorBloco(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}
