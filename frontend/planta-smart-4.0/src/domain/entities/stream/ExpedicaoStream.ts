import { StatusEstacao } from "@enums/StatusEstacao";

export interface ExpedicaoStream {
  estacao: 'expedicao';
  status: StatusEstacao;
  numeroOP: number;
  ocupado: boolean;
  aguardando: boolean;
  manual: boolean;
  emergencia: boolean;
  pedirPosicaoExp: boolean;
  adicionarExpedicao: boolean;
  removerExpedicao: boolean;
  iniciarGuardarExp: boolean;
  recebidoExpedicao: boolean;
  posicaoGuardarExp: number;
  posicaoGuardadoExpedicao: number;
  posicaoRemovidoExpedicao: number;
  opGuardadoExpedicao: number;
  orderExpedicao: number[];
  statusExpedicao: number;
  recebidoOp: boolean;
  startOP: boolean;
  finishOP: boolean;
  cancelOP: boolean;
}
