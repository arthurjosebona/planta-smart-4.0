import { useEstoqueContext } from '@contexts/EstoqueContext';
import { useExpedicaoContext } from '@contexts/ExpedicaoContext';
import { useMonitorContext } from '@contexts/MonitorContext';
import { usePingContext } from '@contexts/PingContext';
import { StatusEstacao } from '@enums/StatusEstacao';
import type { ModuleStatus } from '@components/organisms/EstacoesSection/types';

type StationKey = 'estoque' | 'processo' | 'montagem' | 'expedicao';

function deriveModuleStatus(online: boolean, status: StatusEstacao | undefined): ModuleStatus {
  if (!online) return 'off';
  if (status === StatusEstacao.Ocupado) return 'on';
  return 'pause';
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

  return {
    estoque,
    expedicao,
    monitor,
    moduleStatus,
    erro: estoque.erro ?? expedicao.erro,
    dismissErro,
  };
}
