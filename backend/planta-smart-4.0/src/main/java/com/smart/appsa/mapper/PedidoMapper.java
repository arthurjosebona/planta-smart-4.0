package com.smart.appsa.mapper;

import java.util.ArrayList;

import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.model.Pedido;

public class PedidoMapper {
    public static PedidoResponseDTO mapDto(Pedido pedido) {
        return PedidoResponseDTO.builder()
            .id(pedido.getId())
            .ordemDeProducao(pedido.getOrdemDeProducao())
            .blocos(pedido.getBlocos().stream().map(b -> BlocoMapper.mapDTO(b)).toList()) 
            .status(pedido.getStatus())
            .tipo(pedido.getTipo())
            .corTampa(pedido.getCorTampa())
            .registroCriacao(pedido.getRegistroCriacao())
            .registroEntradaExpedicao(pedido.getRegistroEntradaExpedicao())
            .registroSaidaExpedicao(pedido.getRegistroSaidaExpedicao())
            .expedicao(pedido.getExpedicao())
            .build();
    }

    public static Pedido mapEntityByRequestDTO(PedidoRequestDTO requestDTO) {
        return Pedido.builder()
            .ordemDeProducao(requestDTO.ordemDeProducao())
            .blocos(new ArrayList<>()) // Envia vazio pois é responsabilidade do BlocoService salvar os blocos
            .status(requestDTO.status())
            .tipo(requestDTO.tipo())
            .corTampa(requestDTO.corTampa())
            .build();
    }

    public static PedidoRequestDTO mapRequestDto(Pedido pedido) {
        return PedidoRequestDTO.builder()
            .ordemDeProducao(pedido.getOrdemDeProducao())
            .blocos(pedido.getBlocos()) 
            .status(pedido.getStatus())
            .tipo(pedido.getTipo())
            .corTampa(pedido.getCorTampa())
            .build();
    }

    public static Pedido mapEntityByResponseDTO(PedidoResponseDTO responseDTO) {
        return Pedido.builder()
            .ordemDeProducao(responseDTO.ordemDeProducao())
            .blocos(new ArrayList<>()) // Envia vazio pois é responsabilidade do BlocoService salvar os blocos
            .status(responseDTO.status())
            .tipo(responseDTO.tipo())
            .corTampa(responseDTO.corTampa())
            .build();
    }
}
