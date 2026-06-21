import { CorBloco } from '@enums/CorBloco';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { ConfigLamina } from '@valueObjects/ConfigLamina';

export interface ConfigBloco {
  /** `null` enquanto o bloco está no modo blueprint (cor ainda não escolhida). */
  cor: CorBloco | null;
  laminas: Record<PosicaoLamina, ConfigLamina>;
}
