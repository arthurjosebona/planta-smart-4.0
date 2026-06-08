import { Expedicao } from '@entities/Expedicao';
import { ExpedicaoResponseDTO } from './ExpedicaoResponseDTO';
import { BlocoGetPedidoResponseDTO } from './BlocoGetPedidoResponseDTO';

export interface PedidoGetResponseDTO {
  id: number;
  ordemDeProducao: number;
  blocos: BlocoGetPedidoResponseDTO[];
  status: string;
  tipo: string;
  corTampa: string;
  registroCriacao: string;
  registroEntradaExpedicao: string | null;
  registroSaidaExpedicao: string | null;
  expedicao: ExpedicaoResponseDTO;
}
