import { Pedido } from '@entities/Pedido';

// Estado da fila de produção mantido em memória pelo backend.
// {@code emExecucao} é o pedido sendo produzido (ou null); {@code pendentes}
// são os enfileirados em ordem FIFO; {@code tempoExecucaoSegundos} é o tempo
// decorrido do pedido em execução (0 quando não há pedido em curso).
export interface FilaProducao {
  emExecucao: Pedido | null;
  tempoExecucaoSegundos: number;
  pendentes: Pedido[];
}

export const FilaProducaoInitial: FilaProducao = {
  emExecucao: null,
  tempoExecucaoSegundos: 0,
  pendentes: [],
};
