package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.EnumeratedValue;

public enum StatusEstacao {
    PARADO(0),
    OCUPADO(1),
    AGUARDANDO(2),
    MANUAL(3),
    EMERGENCIA(4);

    @EnumeratedValue
    int value;

    @JsonCreator
    public static StatusEstacao fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (StatusEstacao s : values()) {
            if (parsed.equals(s.value)) return s;
        }
        throw new IllegalArgumentException("StatusEstacao inválido: " + value);
    }

    private StatusEstacao(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}
