import { LaminaCreateRequestDTO } from './LaminaCreateRequestDTO';

export interface BlocoCreateRequestDTO {
  cor: number;
  andar: number;
  laminas: LaminaCreateRequestDTO[];
}
