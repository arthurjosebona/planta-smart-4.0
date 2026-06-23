package com.smart.appsa.dto.clp;

import java.util.Map;

import lombok.Builder;

@Builder
public record StartReadingsResponseDTO(
    Map<String, Boolean> resultados,
    boolean todasConectadas
) {}
