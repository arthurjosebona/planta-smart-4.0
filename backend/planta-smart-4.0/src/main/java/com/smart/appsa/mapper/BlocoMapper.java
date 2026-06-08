package com.smart.appsa.mapper;

import com.smart.appsa.dto.response.BlocoResponseDTO;
import com.smart.appsa.model.Bloco;

public class BlocoMapper {
    public static BlocoResponseDTO mapDTO(Bloco entity) {
        return BlocoResponseDTO
            .builder()
            .id(entity.getId())
            .cor(entity.getCor())
            .andar(entity.getAndar())
            .laminas(entity.getLaminas() != null ? entity.getLaminas().stream().map(l -> LaminaMapper.mapDto(l)).toList() : null)
            .build();
    }
}
