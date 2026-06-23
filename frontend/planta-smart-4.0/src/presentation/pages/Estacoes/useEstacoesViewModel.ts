import { useEstoqueContext } from '@contexts/EstoqueContext';
import { useExpedicaoContext } from '@contexts/ExpedicaoContext';
import { useMonitorContext } from '@contexts/MonitorContext';
import { usePingContext } from '@contexts/PingContext';
import { StatusEstacao } from '@enums/StatusEstacao';
import { CorEstoque } from '@enums/CorEstoque';
import type { Estoque } from '@entities/Estoque';
import type { Expedicao } from '@entities/Expedicao';
import type { ModuleStatus } from '@components/organisms/EstacoesSection/types';

type StationKey = 'estoque' | 'processo' | 'montagem' | 'expedicao';

function deriveModuleStatus(online: boolean, status: StatusEstacao | undefined): ModuleStatus {
  if (!online) return 'off';
  if (status === StatusEstacao.Ocupado) return 'on';
  return 'pause';
}

// Converte o vetor de cores do magazine de estoque recebido via SSE
// (posicoesOcupadas) em entidades Estoque para exibição somente-leitura.
// O índice do vetor corresponde à posição física (1-based) e o valor à cor (0..3).
function mapBancadaEstoque(posicoesOcupadas: number[] | undefined): Estoque[] {
  return (posicoesOcupadas ?? []).map((cor, index) => ({
    id: index + 1,
    posicaoFisica: index + 1,
    cor: (cor >= 0 && cor <= 3 ? cor : 0) as CorEstoque,
  }));
}

// Converte o vetor de ordens de produção do magazine de expedição recebido via SSE
// (orderExpedicao) em entidades Expedicao. Valor 0 indica posição livre (exibida vazia).
function mapBancadaExpedicao(orderExpedicao: number[] | undefined): Expedicao[] {
  return (orderExpedicao ?? []).map((op, index) => ({
    id: index + 1,
    posicaoFisica: index + 1,
    ordemDeProducaoAtual: (op === 0 ? null : op) as unknown as number,
  }));
}

export function useEstacoesViewModel() {
  const estoque = useEstoqueContext();
  const expedicao = useExpedicaoContext();
  const monitor = useMonitorContext();
  const { pingMap } = usePingContext();

  const moduleStatus: Record<StationKey, ModuleStatus> = {
    estoque:   deriveModuleStatus(pingMap.estoque,   monitor.estoque?.status),
    processo:  deriveModuleStatus(pingMap.processo,  monitor.processo?.status),
    montagem:  deriveModuleStatus(pingMap.montagem,  monitor.montagem?.status),
    expedicao: deriveModuleStatus(pingMap.expedicao, monitor.expedicao?.status),
  };

  function dismissErro() {
    estoque.dismissErro();
    expedicao.dismissErro();
  }

  // Conteúdo físico das magazines da bancada, derivado do stream SSE (MonitorContext).
  const bancada = {
    estoque: mapBancadaEstoque(monitor.estoque?.posicoesOcupadas),
    expedicao: mapBancadaExpedicao(monitor.expedicao?.orderExpedicao),
  };

  return {
    estoque,
    expedicao,
    monitor,
    moduleStatus,
    bancada,
    erro: estoque.erro ?? expedicao.erro,
    dismissErro,
  };
}
