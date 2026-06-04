package com.smart.appsa.mapper;

import com.smart.appsa.dto.response.EstoqueResponseDTO;
import com.smart.appsa.model.Estoque;

public class EstoqueMapper {

    public static EstoqueResponseDTO mapDTO(Estoque estoque) {
        return EstoqueResponseDTO.builder()
            .id(estoque.getId())
            .posicaoFisica(estoque.getPosicaoFisica())
            .corEstoque(estoque.getCorEstoque())
            .build();
    }

    public static Estoque mapEntityByResponseDTO(EstoqueResponseDTO dto) {
        return Estoque.builder()
            .id(dto.id())
            .posicaoFisica(dto.posicaoFisica())
            .corEstoque(dto.corEstoque())
            .build();
    }
}
