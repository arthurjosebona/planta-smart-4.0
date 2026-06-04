package com.smart.appsa.dto.response;

import com.smart.appsa.model.enums.CorEstoque;

import lombok.Builder;

@Builder
public record EstoqueResponseDTO(
    Long id,
    Integer posicaoFisica,
    CorEstoque corEstoque
) {}
