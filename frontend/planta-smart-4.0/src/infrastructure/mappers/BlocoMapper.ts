import { BlocoCreateRequestDTO } from '@dtos/request/BlocoCreateRequestDTO';
import { Bloco } from '@entities/Bloco';
import { AndarBloco, AndarBlocoToInt } from '@enums/AndarBloco';
import { CorBlocoToInt } from '@enums/CorBloco';
import { LaminaMapper } from './LaminaMapper';

export const BlocoMapper = {
  mapToCreateRequestDTO(entity: Bloco): BlocoCreateRequestDTO {
    return {
      cor: CorBlocoToInt[entity.cor],
      andar: AndarBlocoToInt[entity.andar],
      laminas: LaminaMapper.mapToCreateRequestDTO(entity.laminas[0]),
    };
  },

  mapBlocosToCreateRequestsDTO(entities: Bloco[]): BlocoCreateRequestDTO[] {
    const requests: BlocoCreateRequestDTO[] = [];

    entities.forEach((entity) => {
      requests.push(this.mapToCreateRequestDTO(entity));
    });

    return requests;
  },
};
