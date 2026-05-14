package com.smart.appsa.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import jakarta.persistence.EnumeratedValue;

public enum StatusPedido {
    PENDENTE(1),
    PRODUCAO(2),
    CONCLUIDO(3);

    @EnumeratedValue
    Integer value;

    @JsonCreator
    public static StatusPedido fromValue(String value) {
        Integer parsed = Integer.valueOf(value);
        for (StatusPedido s : values()) {
            if (parsed.equals(s.value)) return s;
        }
        throw new IllegalArgumentException("StatusPedido inválido: " + value);
    }

    private StatusPedido(Integer value) {
        this.value = value;
    }

    public Integer getValue() { return value; }
}

