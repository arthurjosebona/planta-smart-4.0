import { EstoqueRequestDTO } from '@dtos/request/EstoqueRequestDTO';
import { EstoqueResponseDTO } from '@dtos/response/EstoqueResponseDTO';
import { Estoque } from '@entities/Estoque';
import { CorEstoque } from '@enums/CorEstoque';

const corEstoqueMap: Record<string, CorEstoque> = {
  VAZIO: CorEstoque.Vazio,
  PRETO: CorEstoque.Preto,
  VERMELHO: CorEstoque.Vermelho,
  AZUL: CorEstoque.Azul,
};

export const EstoqueMapper = {
  mapEntityByResponseDTO(dto: EstoqueResponseDTO): Estoque {
    return {
      id: dto.id,
      posicaoFisica: dto.posicaoFisica,
      cor: corEstoqueMap[dto.corEstoque.toUpperCase()],
    };
  },

  mapEntitiesByResponsesDTOs(dtos: EstoqueResponseDTO[]): Estoque[] {
    const responses: Estoque[] = [];

    dtos.forEach((dto) => {
      responses.push(this.mapEntityByResponseDTO(dto));
    });

    return responses;
  },

  mapRequestDTOByEntity(entity: Estoque): EstoqueRequestDTO {
    return {
      id: entity.id,
      posicaoFisica: entity.posicaoFisica,
      corEstoque: entity.cor,
    };
  },

  mapRequestsDTOByEntities(entities: Estoque[]): EstoqueRequestDTO[] {
    const requests: EstoqueRequestDTO[] = [];

    entities.forEach((entity) => {
      requests.push(this.mapRequestDTOByEntity(entity));
    });

    return requests;
  },
};
