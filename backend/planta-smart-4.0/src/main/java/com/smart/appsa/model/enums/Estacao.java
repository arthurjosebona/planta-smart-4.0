package com.smart.appsa.model.enums;

import java.util.Optional;

// Estações da planta que possuem um CLP dedicado.
// O {@code nome} corresponde à chave recebida no payload de {@code /start-readings}.
public enum Estacao {
    ESTOQUE("estoque"),
    PROCESSO("processo"),
    MONTAGEM("montagem"),
    EXPEDICAO("expedicao");

    private final String nome;

    Estacao(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public static Optional<Estacao> fromNome(String nome) {
        if (nome == null) {
            return Optional.empty();
        }
        String alvo = nome.trim().toLowerCase();
        for (Estacao estacao : values()) {
            if (estacao.nome.equals(alvo)) {
                return Optional.of(estacao);
            }
        }
        return Optional.empty();
    }
}
