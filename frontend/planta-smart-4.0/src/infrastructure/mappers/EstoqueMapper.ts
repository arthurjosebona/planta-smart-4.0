import { EstoqueResponseDTO } from '@dtos/response/EstoqueResponseDTO';
import { Estoque } from '@entities/Estoque';

export const EstoqueMapper = {
  mapEntityByResponseDTO(dto: EstoqueResponseDTO): Estoque {
    return {
      id: dto.id,
      posicaoFisica: dto.posicaoFisica,
      cor: dto.corEstoque,
    };
  },

  mapEntitiesByResponsesDTOs(dtos: EstoqueResponseDTO[]): Estoque[] {
    const responses: Estoque[] = [];

    dtos.forEach((dto) => {
      responses.push(this.mapEntityByResponseDTO(dto));
    });

    return responses;
  },

  mapRequestDTOByEntity(entity: Estoque): EstoqueResponseDTO {
    return {
      id: entity.id,
      posicaoFisica: entity.posicaoFisica,
      corEstoque: entity.cor,
    };
  },

  mapRequestsDTOByEntities(entities: Estoque[]): EstoqueResponseDTO[] {
    const requests: EstoqueResponseDTO[] = [];

    entities.forEach((entity) => {
      requests.push(this.mapRequestDTOByEntity(entity));
    });

    return requests;
  },
};
