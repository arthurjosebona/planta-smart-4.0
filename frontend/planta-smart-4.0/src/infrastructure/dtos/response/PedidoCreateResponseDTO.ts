import { Expedicao } from "@entities/Expedicao";

export interface PedidoCreateResponseDTO {
  id: number;
  ordemDeProducao: number;
  status: string;
  tipo: string;
  corTampa: string;
  registroCriacao: string;
  expedicao: Expedicao;
}
