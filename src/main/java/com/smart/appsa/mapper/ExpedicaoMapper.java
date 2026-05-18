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

}
