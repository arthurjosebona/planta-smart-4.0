package com.smart.appsa.mapper;

import java.util.ArrayList;

import com.smart.appsa.dto.clp.PedidoConfigDTO;
import com.smart.appsa.dto.clp.PedidoInfoDTO;
import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.exception.core.BusinessException;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.AndarBloco;
import com.smart.appsa.model.enums.PosicaoLamina;

public class PedidoMapper {
    private static final String IP_CLP_1 = "10.74.241.10";

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
            .registroEntradaEstoque(pedido.getRegistroEntradaEstoque())
            .registroEntradaProcesso(pedido.getRegistroEntradaProcesso())
            .registroEntradaMontagem(pedido.getRegistroEntradaMontagem())
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

    public static PedidoInfoDTO mapToInfoDTOByEntity(Pedido entity) {
        Bloco bloco1 = entity.getBlocos()
            .stream()
            .filter(b -> b.getAndar() == AndarBloco.PRIMEIRO)
            .findFirst()
            .orElseThrow(() -> new BusinessException("Pedido não possui bloco com primeiro andar"));

        Bloco bloco2 = entity.getBlocos()
            .stream()
            .filter(b -> b.getAndar() == AndarBloco.SEGUNDO)   
            .findFirst()
            .orElse(null);

        Bloco bloco3 = entity.getBlocos()
            .stream()
            .filter(b -> b.getAndar() == AndarBloco.TERCEIRO)  
            .findFirst()
            .orElse(null);

        return PedidoInfoDTO
            .builder()
            .corAndar1(bloco1.getCor().getValue())
            .posicaoEstoqueAndar1(bloco1.getEstoque().getPosicaoFisica())
            .corLamina1Andar1(bloco1.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.ESQUERDA)
                .mapToInt(l -> l.getCor().getValue())
                .findFirst().orElse(0))
            .corLamina2Andar1(bloco1.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.FRENTE)
                .mapToInt(l -> l.getCor().getValue())
                .findFirst().orElse(0))
            .corLamina3Andar1(bloco1.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.DIREITA)
                .mapToInt(l -> l.getCor().getValue())
                .findFirst().orElse(0))
            .padraoLamina1Andar1(bloco1.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.ESQUERDA)
                .mapToInt(l -> l.getPadrao().getValue())
                .findFirst().orElse(0))
            .padraoLamina2Andar1(bloco1.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.FRENTE)
                .mapToInt(l -> l.getPadrao().getValue())
                .findFirst().orElse(0))
            .padraoLamina3Andar1(bloco1.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.DIREITA)
                .mapToInt(l -> l.getPadrao().getValue())
                .findFirst().orElse(0))
            .corAndar2(bloco2 != null ? bloco2.getCor().getValue() : 0)
            .posicaoEstoqueAndar2(bloco2 != null ? bloco2.getEstoque().getPosicaoFisica() : 0)
            .corLamina1Andar2(bloco2 != null ? bloco2.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.ESQUERDA)
                .mapToInt(l -> l.getCor().getValue())
                .findFirst().orElse(0) : 0)
            .corLamina2Andar2(bloco2 != null ? bloco2.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.FRENTE)
                .mapToInt(l -> l.getCor().getValue())
                .findFirst().orElse(0) : 0)
            .corLamina3Andar2(bloco2 != null ? bloco2.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.DIREITA)
                .mapToInt(l -> l.getCor().getValue())
                .findFirst().orElse(0) : 0)
            .padraoLamina1Andar2(bloco2 != null ? bloco2.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.ESQUERDA)
                .mapToInt(l -> l.getPadrao().getValue())
                .findFirst().orElse(0) : 0)
            .padraoLamina2Andar2(bloco2 != null ? bloco2.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.FRENTE)
                .mapToInt(l -> l.getPadrao().getValue())
                .findFirst().orElse(0) : 0)
            .padraoLamina3Andar2(bloco2 != null ? bloco2.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.DIREITA)
                .mapToInt(l -> l.getPadrao().getValue())
                .findFirst().orElse(0) : 0)
            .corAndar3(bloco3 != null ? bloco3.getCor().getValue() : 0)
            .posicaoEstoqueAndar3(bloco3 != null ? bloco3.getEstoque().getPosicaoFisica() : 0)
            .corLamina1Andar3(bloco3 != null ? bloco3.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.ESQUERDA)
                .mapToInt(l -> l.getCor().getValue())
                .findFirst().orElse(0) : 0)
            .corLamina2Andar3(bloco3 != null ? bloco3.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.FRENTE)
                .mapToInt(l -> l.getCor().getValue())
                .findFirst().orElse(0) : 0)
            .corLamina3Andar3(bloco3 != null ? bloco3.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.DIREITA)
                .mapToInt(l -> l.getCor().getValue())
                .findFirst().orElse(0) : 0)
            .padraoLamina1Andar3(bloco3 != null ? bloco3.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.ESQUERDA)
                .mapToInt(l -> l.getPadrao().getValue())
                .findFirst().orElse(0) : 0)
            .padraoLamina2Andar3(bloco3 != null ? bloco3.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.FRENTE)
                .mapToInt(l -> l.getPadrao().getValue())
                .findFirst().orElse(0) : 0)
            .padraoLamina3Andar3(bloco3 != null ? bloco3.getLaminas().stream()
                .filter(l -> l.getPosicao() == PosicaoLamina.DIREITA)
                .mapToInt(l -> l.getPadrao().getValue())
                .findFirst().orElse(0) : 0)
            .numeroPedido(entity.getOrdemDeProducao())
            .andares(entity.getTipo().getValue())
            .posicaoExpedicao(entity.getExpedicao().getPosicaoFisica())
            .build();
    }

    public static PedidoConfigDTO mapToConfigDTOByEntity(Pedido entity) {
        return PedidoConfigDTO
            .builder()
            .idPedido(entity.getId())
            .tipoPedido(entity.getTipo().getValue())
            .tampaPedido(entity.getCorTampa().getValue())
            .ipClp(IP_CLP_1)
            .build();
    }
}
