package com.smart.appsa.dto.response;

import lombok.Builder;

@Builder
public record ExpedicaoResponseDTO(
    Long id,
    Integer posicaoFisica,
    Integer ordemDeProducaoAtual
) {}
