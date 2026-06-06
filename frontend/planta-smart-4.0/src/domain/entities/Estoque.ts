import { CorEstoque } from '@enums/CorEstoque';

export interface Estoque {
  id: number;
  posicaoFisica: number;
  cor: CorEstoque;
}
