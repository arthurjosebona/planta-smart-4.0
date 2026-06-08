import { LaminaGetResponseDto } from './LaminaGetResponseDTO';

export interface BlocoGetPedidoResponseDTO {
  id: number;
  cor: string;
  andar: string;
  laminas: LaminaGetResponseDto[];
}
