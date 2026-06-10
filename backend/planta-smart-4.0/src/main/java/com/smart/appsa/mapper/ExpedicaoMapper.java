package com.smart.appsa.mapper;

import com.smart.appsa.dto.response.ExpedicaoResponseDTO;
import com.smart.appsa.model.Expedicao;

public class ExpedicaoMapper {

    public static ExpedicaoResponseDTO mapDto(Expedicao expedicao) {
        return ExpedicaoResponseDTO.builder()
            .id(expedicao.getId())
            .ordemDeProducaoAtual(expedicao.getOrdemDeProducaoAtual())
            .posicaoFisica(expedicao.getPosicaoFisica())
            .build();
    }

    public static Expedicao mapEntityByResponseDTO(ExpedicaoResponseDTO dto) {
        return Expedicao.builder()
            .id(dto.id())
            .ordemDeProducaoAtual(dto.ordemDeProducaoAtual())
            .posicaoFisica(dto.posicaoFisica())
            .build();
    }
}
