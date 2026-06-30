import { useIpsViewModel } from '@pages/Ips/useIpsViewModel';
import { useConexaoStatus } from '../../hook/useConexaoStatus';
import { usePingContext } from '@contexts/PingContext';
import { useMonitorContext } from '@contexts/MonitorContext';
import { useEstoqueContext } from '@contexts/EstoqueContext';
import { useExpedicaoContext } from '@contexts/ExpedicaoContext';
import { CorEstoque } from '@enums/CorEstoque';
import { HomeStation, HomeSummary } from '@pages/Home/HomeModel';

/**
 * ViewModel da Home: apresenta os detalhes da bancada em tempo real — status de
 * conexão, ping dos CLPs, status operacional vindo do stream (SSE) e ocupação de
 * estoque/expedição. A configuração/conexão em si vive na tela de Conexão (IPs).
 */
export function useHomeViewModel() {
  const ips = useIpsViewModel(); // fonte dos IPs e do modo readOnly (consultados do backend)
  const conectado = useConexaoStatus(); // status global de conexão (definido na tela de Conexão)
  const { pingMap } = usePingContext();
  const monitor = useMonitorContext();
  const estoque = useEstoqueContext();
  const expedicao = useExpedicaoContext();

  const streamByKey = {
    estoque: monitor.estoque,
    processo: monitor.processo,
    montagem: monitor.montagem,
    expedicao: monitor.expedicao,
  };

  // Detalhes da bancada: módulo configurado + ping + status do stream.
  const stations: HomeStation[] = ips.model.modulos.map((modulo) => {
    const stream = streamByKey[modulo.key];
    return {
      key: modulo.key,
      label: modulo.label,
      ip: modulo.ip,
      online: pingMap[modulo.key],
      status: stream?.status ?? null,
      numeroOP: stream?.numeroOP ?? null,
      emergencia: stream?.emergencia ?? false,
      manual: stream?.manual ?? false,
    };
  });

  const estoqueOcupado = estoque.estoque.filter((b) => b.cor !== CorEstoque.Vazio).length;
  const expedicaoOcupada = expedicao.expedicao.filter((s) => s.ordemDeProducaoAtual > 0).length;

  const summary: HomeSummary = {
    restConectado: conectado,
    streamConectado: monitor.conectado,
    readOnly: ips.model.readOnly,
    estoqueOcupado,
    estoqueTotal: estoque.estoque.length,
    expedicaoOcupada,
    expedicaoTotal: expedicao.expedicao.length,
    estacoesOnline: stations.filter((s) => s.online).length,
    estacoesTotal: stations.length,
  };

  return { conectado, stations, summary };
}
