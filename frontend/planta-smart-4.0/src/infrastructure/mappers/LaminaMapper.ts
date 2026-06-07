import { LaminaCreateRequestDTO } from '@dtos/request/LaminaCreateRequestDTO';
import { LaminaGetResponseDto } from '@dtos/response/LaminaGetResponseDTO';
import { Lamina } from '@entities/Lamina';
import { CorLamina, CorLaminaStringToEnum, CorLaminaToInt } from '@enums/CorLamina';
import { PadraoLaminaStringToEnum, PadraoLaminaToInt } from '@enums/PadraoLamina';
import { PosicaoLaminaStringToEnum, PosicaoLaminaToInt } from '@enums/PosicaoLamina';

export const LaminaMapper = {
  mapToCreateRequestDTO(entity: Lamina): LaminaCreateRequestDTO {
    return {
      cor: CorLaminaToInt[entity.cor],
      padrao: PadraoLaminaToInt[entity.padrao],
      posicao: PosicaoLaminaToInt[entity.posicao],
    };
  },

  mapLaminasToCreateRequestsDTO(entities: Lamina[]): LaminaCreateRequestDTO[] {
    const requests: LaminaCreateRequestDTO[] = [];

    entities.forEach((entity) => {
      requests.push(this.mapToCreateRequestDTO(entity));
    });

    return requests;
  },

  mapLaminaByGetDTO(dto: LaminaGetResponseDto): Lamina {
    return {
      id: dto.id,
      cor: CorLaminaStringToEnum[dto.cor.toUpperCase()],
      padrao: PadraoLaminaStringToEnum[dto.padrao.toUpperCase()],
      posicao: PosicaoLaminaStringToEnum[dto.posicao.toUpperCase()],
    };
  },

  mapLaminasByGetDTO(dtos: LaminaGetResponseDto[]): Lamina[] {
    const entities: Lamina[] = [];

    dtos.forEach((dto) => {
      entities.push(this.mapLaminaByGetDTO(dto));
    });

    return entities;
  },
};
