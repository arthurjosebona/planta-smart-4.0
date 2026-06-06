import { CorLamina } from '@enums/CorLamina';
import { PadraoLamina } from '@enums/PadraoLamina';
import { PosicaoLamina } from '@enums/PosicaoLamina';

export interface Lamina {
  id: number | null;
  cor: CorLamina;
  padrao: PadraoLamina;
  posicao: PosicaoLamina;
}
