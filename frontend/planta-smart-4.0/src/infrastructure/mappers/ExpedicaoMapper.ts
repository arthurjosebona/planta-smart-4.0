import { ExpedicaoRequestDTO } from '@dtos/request/ExpedicaoRequestDTO';
import { ExpedicaoResponseDTO } from '@dtos/response/ExpedicaoResponseDTO';
import { Expedicao } from '@entities/Expedicao';

export const ExpedicaoMapper = {
  mapEntityByResponseDTO(dto: ExpedicaoResponseDTO): Expedicao {
    return {
      id: dto.id,
      posicaoFisica: dto.posicaoFisica,
      ordemDeProducaoAtual: dto.ordemDeProducaoAtual,
    };
  },

  mapEntitiesByResponsesDTOs(dtos: ExpedicaoResponseDTO[]): Expedicao[] {
    const responses: Expedicao[] = [];

    dtos.forEach((dto) => {
      responses.push(this.mapEntityByResponseDTO(dto));
    });

    return responses;
  },

  mapRequestDTOByEntity(entity: Expedicao): ExpedicaoRequestDTO {
    return {
      id: entity.id,
      posicaoFisica: entity.posicaoFisica,
      ordemDeProducao: entity.ordemDeProducaoAtual,
    }
  },

  mapRequestDTOsByEntities(entities: Expedicao[]): ExpedicaoRequestDTO[] {
    const responses: ExpedicaoRequestDTO[] = [];

    entities.forEach((entity) => {
      responses.push(this.mapRequestDTOByEntity(entity));
    })

    return responses;
  },
};
