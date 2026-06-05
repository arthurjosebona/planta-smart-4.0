import { Bloco } from '@entities/Bloco';

export interface PedidoCreateResponseDTO {
  ordemDeProducao: number;
  blocos: Bloco[];
  tipo: number;
  corTampa: number;
}
