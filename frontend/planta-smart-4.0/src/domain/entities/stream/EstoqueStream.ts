import { StatusEstacao } from "@enums/StatusEstacao";

export interface EstoqueStream {
  estacao: 'estoque';
  status: StatusEstacao;
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
  recebidoOp: boolean;
  startOP: boolean;
  finishOP: boolean;
  cancelOP: boolean;
  registroInicioPedido: string;
}