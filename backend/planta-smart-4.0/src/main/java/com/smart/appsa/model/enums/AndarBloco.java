package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.EnumeratedValue;

public enum AndarBloco {
    PRIMEIRO(1),
    SEGUNDO(2),
    TERCEIRO(3);

    @EnumeratedValue
    int value;

    @JsonCreator
    public static AndarBloco fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (AndarBloco p : values()) {
            if (parsed.equals(p.value)) return p;
        }
        throw new IllegalArgumentException("AndarBloco inválido: " + value);
    }

    private AndarBloco(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}
