package com.smart.appsa.dto.request;

import com.smart.appsa.model.enums.CorEstoque;

import lombok.Builder;

@Builder
public record EstoqueRequestDTO (
    Long id,
    Integer posicaoFisica,
    CorEstoque corEstoque
) {}
