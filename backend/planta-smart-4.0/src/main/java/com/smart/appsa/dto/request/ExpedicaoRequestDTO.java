package com.smart.appsa.dto.request;

import lombok.Builder;

@Builder
public record ExpedicaoRequestDTO(
    Long id,
    Integer posicaoFisica,
    Integer ordemDeProducao
) {

}
