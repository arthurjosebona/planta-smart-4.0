import { Pedido } from '@entities/Pedido';
import { PedidoCreateRequestDTO } from '@dtos/request/PedidoCreateRequestDTO';
import { TipoPedido, TipoPedidoStringToEnum, TipoPedidoToInt } from '@enums/TipoPedido';
import { CorTampa, CorTampaStringToEnum, CorTampaToInt } from '@enums/CorTampa';
import { BlocoMapper } from './BlocoMapper';
import { StatusPedido, StatusPedidoStringToEnum } from '@enums/StatusPedido';
import { PedidoCreateResponseDTO } from '@dtos/response/PedidoCreateResponseDTO';

export const PedidoMapper = {
  mapToCreateRequestDTO(entity: Pedido): PedidoCreateRequestDTO {
    return {
      ordemDeProducao: entity.ordemDeProducao,
      tipo: TipoPedidoToInt[entity.tipo],
      corTampa: CorTampaToInt[entity.corTampa],
      blocos: BlocoMapper.mapBlocosToCreateRequestsDTO(entity.blocos),
    };
  },

  mapToEntity(dto: PedidoCreateResponseDTO): Pedido {
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
    };
  },
};
