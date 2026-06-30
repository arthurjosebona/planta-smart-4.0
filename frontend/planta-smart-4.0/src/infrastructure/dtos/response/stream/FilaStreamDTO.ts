import { PedidoGetResponseDTO } from '@dtos/response/PedidoGetResponseDTO';

// Espelha o FilaStreamDTO do backend (snapshot da fila de produção).
// Recebido tanto via REST (GET /api/smart/fila) quanto via SSE (evento "fila").
export interface FilaStreamDTO {
  emExecucao: PedidoGetResponseDTO | null;
  tempoExecucaoSegundos: number;
  pendentes: PedidoGetResponseDTO[];
}
