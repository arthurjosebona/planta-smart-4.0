package com.smart.appsa.dto.clp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PedidoConfigDTO {

    @JsonProperty("Id_Pedido")
    private Long idPedido;
    @JsonProperty("Tipo_Pedido")
    private int tipoPedido;
    @JsonProperty("Tampa_Pedido")
    private int tampaPedido;
    @JsonProperty("Ip_CLP")
    private String ipClp;
}
