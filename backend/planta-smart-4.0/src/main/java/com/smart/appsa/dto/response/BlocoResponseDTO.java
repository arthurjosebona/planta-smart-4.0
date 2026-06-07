package com.smart.appsa.dto.response;

import java.util.List;

import com.smart.appsa.model.enums.AndarBloco;
import com.smart.appsa.model.enums.CorBloco;

import lombok.Builder;

@Builder
public record BlocoResponseDTO(
    Long id,
    CorBloco cor,
    AndarBloco andar,
    List<LaminaResponseDTO> laminas
) {}