package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.EnumeratedValue;

public enum CorEstoque {
    VAZIO(0),
    PRETO(1),
    VERMELHO(2),
    AZUL(3);

    @EnumeratedValue
    int value;

    @JsonCreator
    public static CorEstoque fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (CorEstoque c : values()) {
            if (parsed.equals(c.value)) return c;
        }
        throw new IllegalArgumentException("CorEstoque inválida: " + value);
    }   

    public static CorEstoque fromValue(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("CorEstoque não pode ser nula");
        }
        for (CorEstoque cor : values()) {
            if (cor.value == value) {
                return cor;
            }
        }
        throw new IllegalArgumentException("CorEstoque inválida: " + value);
    }

    private CorEstoque(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}
