import { Pedido } from '@entities/Pedido';
import { PedidoCreateRequestDTO } from '@dtos/request/PedidoCreateRequestDTO';
import { TipoPedido, TipoPedidoStringToEnum, TipoPedidoToInt } from '@enums/TipoPedido';
import { CorTampa, CorTampaStringToEnum, CorTampaToInt } from '@enums/CorTampa';
import { BlocoMapper } from './BlocoMapper';
import { StatusPedido, StatusPedidoStringToEnum } from '@enums/StatusPedido';
import { PedidoCreateResponseDTO } from '@dtos/response/PedidoCreateResponseDTO';
import { PedidoGetResponseDTO } from '@dtos/response/PedidoGetResponseDTO';

export const PedidoMapper = {
  mapToCreateRequestDTO(entity: Pedido): PedidoCreateRequestDTO {
    return {
      ordemDeProducao: entity.ordemDeProducao,
      tipo: TipoPedidoToInt[entity.tipo],
      corTampa: CorTampaToInt[entity.corTampa],
      blocos: BlocoMapper.mapBlocosToCreateRequestsDTO(entity.blocos),
    };
  },

  mapToEntityByCreateDTO(dto: PedidoCreateResponseDTO): Pedido {
    return {
      id: dto.id,
      ordemDeProducao: dto.ordemDeProducao,
      status: StatusPedidoStringToEnum[dto.status],
      tipo: TipoPedidoStringToEnum[dto.tipo],
      corTampa: CorTampaStringToEnum[dto.corTampa],
      registroCriacao: new Date(dto.registroCriacao).toISOString(),
      registroEntradaExpedicao: null,
      registroSaidaExpedicao: null,
      blocos: [],
      expedicao: dto.expedicao,
    };
  },

  mapToEntityByGetDTO(dto: PedidoGetResponseDTO): Pedido {
    return {
      id: dto.id,
      ordemDeProducao: dto.ordemDeProducao,
      blocos: BlocoMapper.mapToEntitiesByGetDTOs(dto.blocos),
      status: StatusPedidoStringToEnum[dto.status],
      tipo: TipoPedidoStringToEnum[dto.tipo],
      corTampa: CorTampaStringToEnum[dto.corTampa],
      expedicao: dto.expedicao,
      registroCriacao: dto.registroCriacao,
      registroEntradaExpedicao: dto.registroEntradaExpedicao,
      registroSaidaExpedicao: dto.registroSaidaExpedicao,
    };
  },

  mapToEntitiesByGetDTOs(dtos: PedidoGetResponseDTO[]): Pedido[] {
    const entities: Pedido[] = [];

    dtos.forEach((dto) => {
      entities.push(this.mapToEntityByGetDTO(dto));
    });

    return entities;
  },
};
