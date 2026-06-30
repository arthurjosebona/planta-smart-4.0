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

    public static Lamina mapEntityByResponseDTO(LaminaResponseDTO dto) {
        return Lamina.builder()
            .id(dto.id())
            .cor(dto.cor())
            .padrao(dto.padrao())
            .posicao(dto.posicao())
            .build();
    }
}
