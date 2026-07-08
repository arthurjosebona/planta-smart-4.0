import { StatusEstacao } from "@enums/StatusEstacao";

export interface MontagemStream {
  estacao: 'montagem';
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
  supervisorioEstoque: string;
  supervisorioProcesso: string;
  supervisorioMontagem: string;
  supervisorioExpedicao: string;
}