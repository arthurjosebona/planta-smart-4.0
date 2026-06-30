import type { StationKey } from '@contexts/PingContext';
import { StatusEstacao } from '@enums/StatusEstacao';

/** Detalhe de um módulo/estação da bancada exibido na Home. */
export interface HomeStation {
  key: StationKey;
  label: string;
  ip: string;
  /** Status de rede vindo do ping dos CLPs. */
  online: boolean;
  /** Status operacional em tempo real vindo do stream (SSE); null se sem dado. */
  status: StatusEstacao | null;
  /** Ordem de produção atualmente na estação (0/null = nenhuma). */
  numeroOP: number | null;
  emergencia: boolean;
  manual: boolean;
}

/** Indicadores agregados da bancada exibidos no topo da Home. */
export interface HomeSummary {
  restConectado: boolean;
  streamConectado: boolean;
  readOnly: boolean;
  estoqueOcupado: number;
  estoqueTotal: number;
  expedicaoOcupada: number;
  expedicaoTotal: number;
  estacoesOnline: number;
  estacoesTotal: number;
}
