import { CorLamina } from '../enums/CorLamina';
import { PadraoLamina } from '../enums/PadraoLamina';

export interface ConfigLamina {
  cor: CorLamina | null;
  padrao: PadraoLamina | null;
}
