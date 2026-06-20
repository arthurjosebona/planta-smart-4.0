export type ClpStatus = 'OCUPADO' | 'AGUARDANDO' | 'MANUAL' | 'EMERGENCIA';

export interface EstoqueStreamDTO {
  estacao: 'estoque';
  status: ClpStatus;
  numeroOP: number;
  ocupado: boolean;
  aguardando: boolean;
  manual: boolean;
  emergencia: boolean;
  iniciarPedido: boolean;
  pedirPosicaoEst: boolean;
  adicionarEstoque: boolean;
  removerEstoque: boolean;
  retornoEstoqueCheio: boolean;
  recebidoEstoque: boolean;
  iniciarGuardarEst: boolean;
  posicaoEstoque: number;
  posicaoGuardarEst: number;
  corGuardarEstoque: number;
  posicoesOcupadas: number[];
  statusEstoque: number;
  statusProcesso: number;
  statusMontagem: number;
  statusExpedicao: number;
  statusProducao: number;
  pedidoEmCurso: boolean;
}

export interface ProcessoMontagemStreamDTO {
  estacao: 'processo' | 'montagem';
  status: ClpStatus;
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

export interface ExpedicaoStreamDTO {
  estacao: 'expedicao';
  status: ClpStatus;
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
}

export type ClpStreamDTO = EstoqueStreamDTO | ProcessoMontagemStreamDTO | ExpedicaoStreamDTO;
