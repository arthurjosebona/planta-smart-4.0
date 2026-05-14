package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.EnumeratedValue;

public enum CorTampa {
    PRETO(1),
    VERMELHO(2),
    AZUL(3);

    @EnumeratedValue
    int value;

    @JsonCreator
    public static CorTampa fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (CorTampa c : values()) {
            if (parsed.equals(c.value)) return c;
        }
        throw new IllegalArgumentException("CorTampa inválida: " + value);
    }

    private CorTampa(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}

