import { Pedido } from '@entities/Pedido';
import { PedidoCreateRequestDTO } from '@dtos/request/PedidoCreateRequestDTO';
import { TipoPedidoToInt } from '@enums/TipoPedido';
import { CorTampaToInt } from '@enums/CorTampa';
import { BlocoMapper } from './BlocoMapper';

export const PedidoMapper = {
  mapToCreateRequestDTO(entity: Pedido): PedidoCreateRequestDTO {
    return {
      ordemDeProducao: entity.ordemDeProducao,
      tipo: TipoPedidoToInt[entity.tipo],
      corTampa: CorTampaToInt[entity.corTampa],
      blocos: BlocoMapper.mapBlocosToCreateRequestsDTO(entity.blocos),
    };
  },
};
