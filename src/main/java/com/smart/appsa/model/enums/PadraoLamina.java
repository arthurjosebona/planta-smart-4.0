package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.EnumeratedValue;

public enum PadraoLamina {
    NENHUM(0),
    CASA(1),
    NAVIO(2),
    ESTRELA(3);

    @EnumeratedValue
    Integer value;

    @JsonCreator
    public static PadraoLamina fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (PadraoLamina p : values()) {
            if (parsed.equals(p.value)) return p;
        }
        throw new IllegalArgumentException("PadraoLamina inválido: " + value);
    }

    private PadraoLamina(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}
