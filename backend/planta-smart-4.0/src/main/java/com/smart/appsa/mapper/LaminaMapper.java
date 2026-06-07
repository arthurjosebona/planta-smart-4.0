package com.smart.appsa.mapper;

import com.smart.appsa.dto.response.LaminaResponseDTO;
import com.smart.appsa.model.Lamina;

public class LaminaMapper {
    public static LaminaResponseDTO mapDto(Lamina entity) {
        return LaminaResponseDTO
            .builder()
            .id(entity.getId())
            .cor(entity.getCor())
            .padrao(entity.getPadrao())
            .posicao(entity.getPosicao())
            .build();
    }
}
