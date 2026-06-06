import { Bloco } from '@entities/Bloco';

export interface PedidoCreateResponseDTO {
  id: number;
  ordemDeProducao: number;
  status: string;
  tipo: string;
  corTampa: string;
  registroCriacao: string;
}
