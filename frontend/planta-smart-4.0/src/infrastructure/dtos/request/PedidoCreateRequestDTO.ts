import { BlocoCreateRequestDTO } from './BlocoCreateRequestDTO';

export interface PedidoCreateRequestDTO {
  ordemDeProducao: number;
  blocos: BlocoCreateRequestDTO[];
  tipo: number;
  corTampa: number;
}
