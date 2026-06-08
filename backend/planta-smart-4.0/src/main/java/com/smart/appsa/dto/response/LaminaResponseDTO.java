package com.smart.appsa.dto.response;

import com.smart.appsa.model.enums.CorLamina;
import com.smart.appsa.model.enums.PadraoLamina;
import com.smart.appsa.model.enums.PosicaoLamina;

import lombok.Builder;

@Builder
public record LaminaResponseDTO(
    Long id,
    CorLamina cor,
    PadraoLamina padrao,
    PosicaoLamina posicao
) {}