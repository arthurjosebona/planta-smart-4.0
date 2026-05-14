package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.EnumeratedValue;

public enum TipoPedido {
    SIMPLES(1),
    DUPLO(2),
    TRIPLO(3);

    @EnumeratedValue
    int value;

    @JsonCreator
    public static TipoPedido fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (TipoPedido t : values()) {
            if (parsed.equals(t.value)) return t;
        }
        throw new IllegalArgumentException("TipoPedido inválido: " + value);
    }

    private TipoPedido(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}
