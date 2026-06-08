package com.smart.appsa.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import lombok.Builder;

@Builder
public record PedidoResponseDTO(
    Long id,
    int ordemDeProducao,
    List<BlocoResponseDTO> blocos,
    StatusPedido status,
    TipoPedido tipo,
    CorTampa corTampa,
    LocalDateTime registroCriacao,
    LocalDateTime registroEntradaExpedicao,
    LocalDateTime registroSaidaExpedicao,
    Expedicao expedicao
) {}
