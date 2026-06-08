import { BlocoCreateRequestDTO } from '@dtos/request/BlocoCreateRequestDTO';
import { Bloco } from '@entities/Bloco';
import { AndarBloco, AndarBlocoStringToEnum, AndarBlocoToInt } from '@enums/AndarBloco';
import { CorBlocoStringToEnum, CorBlocoToInt } from '@enums/CorBloco';
import { LaminaMapper } from './LaminaMapper';
import { BlocoGetPedidoResponseDTO } from '@dtos/response/BlocoGetPedidoResponseDTO';

export const BlocoMapper = {
  mapToCreateRequestDTO(entity: Bloco): BlocoCreateRequestDTO {
    return {
      cor: CorBlocoToInt[entity.cor],
      andar: AndarBlocoToInt[entity.andar],
      laminas: LaminaMapper.mapLaminasToCreateRequestsDTO(entity.laminas),
    };
  },

  mapBlocosToCreateRequestsDTO(entities: Bloco[]): BlocoCreateRequestDTO[] {
    const requests: BlocoCreateRequestDTO[] = [];

    entities.forEach((entity) => {
      requests.push(this.mapToCreateRequestDTO(entity));
    });

    return requests;
  },

  mapToEntityByGetDTO(dto: BlocoGetPedidoResponseDTO): Bloco {
    return {
      id: dto.id,
      cor: CorBlocoStringToEnum[dto.cor.toUpperCase()],
      andar: AndarBlocoStringToEnum[dto.andar.toUpperCase()],
      laminas: LaminaMapper.mapLaminasByGetDTO(dto.laminas),
    };
  },

  mapToEntitiesByGetDTOs(dtos: BlocoGetPedidoResponseDTO[]): Bloco[] {
    const entities: Bloco[] = [];

    dtos.forEach((dto) => {
      entities.push(this.mapToEntityByGetDTO(dto));
    });

    return entities;
  },
};
