import { FilaProducao } from '@entities/FilaProducao';
import { FilaStreamDTO } from '@dtos/response/stream/FilaStreamDTO';
import { PedidoMapper } from './PedidoMapper';

export const FilaProducaoMapper = {
  mapToEntityByStreamDTO(dto: FilaStreamDTO): FilaProducao {
    return {
      emExecucao: dto.emExecucao ? PedidoMapper.mapToEntityByGetDTO(dto.emExecucao) : null,
      tempoExecucaoSegundos: dto.tempoExecucaoSegundos ?? 0,
      pendentes: PedidoMapper.mapToEntitiesByGetDTOs(dto.pendentes ?? []),
    };
  },
};
