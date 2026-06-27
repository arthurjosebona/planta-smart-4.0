export interface ExpedicaoStreamDTO {
  estacao: 'expedicao';
  status: string;
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