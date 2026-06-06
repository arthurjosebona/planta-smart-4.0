import { CorEstoque } from '@enums/CorEstoque';

export interface EstoqueResponseDTO {
  id: number;
  posicaoFisica: number;
  corEstoque: CorEstoque;
}
