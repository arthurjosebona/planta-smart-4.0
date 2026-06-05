import { CorBloco } from '../enums/CorBloco';
import { PosicaoLamina } from '../enums/PosicaoLamina';
import { ConfigLamina } from './ConfigLamina';

export interface ConfigBloco {
  cor: CorBloco;
  laminas: Record<PosicaoLamina, ConfigLamina>;
}
