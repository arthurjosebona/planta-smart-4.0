import { StatusEstacao } from "@enums/StatusEstacao";

export interface ProcessoMontagemStream {
  estacao: 'processo' | 'montagem';
  status: StatusEstacao;
  numeroOP: number;
  ocupado: boolean;
  aguardando: boolean;
  manual: boolean;
  emergencia: boolean;
  recebidoOp: boolean;
  startOP: boolean;
  finishOP: boolean;
  cancelOP: boolean;
  statusBancada: number;
}