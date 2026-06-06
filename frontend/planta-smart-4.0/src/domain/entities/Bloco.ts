import { AndarBloco } from '@enums/AndarBloco';
import { CorBloco } from '@enums/CorBloco';
import { Lamina } from './Lamina';

export interface Bloco {
  id: number | null;
  cor: CorBloco;
  andar: AndarBloco;
  laminas: Lamina[];
}
