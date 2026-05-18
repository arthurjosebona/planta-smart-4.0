package com.smart.appsa.dto.request;

import java.util.List;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import lombok.Builder;

@Builder
public record PedidoRequestDTO(
    int ordemDeProducao,
    List<Bloco> blocos,
    StatusPedido status,
    TipoPedido tipo,
    CorTampa corTampa
) {}
