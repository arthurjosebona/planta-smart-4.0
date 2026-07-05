import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { MonitorModel, MonitorModelInitial } from '@pages/Monitor/MonitorModel';
import { EstoqueStreamDTO } from '@dtos/response/stream/EstoqueStreamDTO';
import { ExpedicaoStreamDTO } from '@dtos/response/stream/ExpedicaoStreamDTO';
import { EstacaoStreamMapper } from '@mappers/EstacaoStreamMapper';
import { ProcessoStreamDTO } from '@dtos/response/stream/ProcessoStreamDTO';
import { MontagemStreamDTO } from '@dtos/response/stream/MontagemStreamDTO';

const SSE_URL = import.meta.env.VITE_BASE_URL + '/api/smart/stream';

type MonitorContextValue = MonitorModel;

const MonitorContext = createContext<MonitorContextValue | null>(null);

export function MonitorProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<MonitorModel>(MonitorModelInitial);

  useEffect(() => {
    const source = new EventSource(SSE_URL);

    source.onopen = () => setState((s) => ({ ...s, conectado: true }));
    source.onerror = () => setState((s) => ({ ...s, conectado: false }));

    source.addEventListener('estoque', (e) => {
      try {
        const dto = JSON.parse((e as MessageEvent).data) as EstoqueStreamDTO;
        setState((s) => ({ ...s, estoque: EstacaoStreamMapper.mapEstoqueByDTO(dto) }));
      } catch (err) {
        console.error('Erro ao parsear evento estoque:', err);
      }
    });

    source.addEventListener('processo', (e) => {
      try {
        const dto = JSON.parse((e as MessageEvent).data) as ProcessoStreamDTO;
        setState((s) => ({ ...s, processo: EstacaoStreamMapper.mapProcessoByDTO(dto) }));
      } catch (err) {
        console.error('Erro ao parsear evento processo:', err);
      }
    });

    source.addEventListener('montagem', (e) => {
      try {
        const dto = JSON.parse((e as MessageEvent).data) as MontagemStreamDTO;
        setState((s) => ({ ...s, montagem: EstacaoStreamMapper.mapMontagemByDTO(dto) }));
      } catch (err) {
        console.error('Erro ao parsear evento montagem:', err);
      }
    });

    source.addEventListener('expedicao', (e) => {
      try {
        const dto = JSON.parse((e as MessageEvent).data) as ExpedicaoStreamDTO  ;
        setState((s) => ({ ...s, expedicao: EstacaoStreamMapper.mapExpedicaoByDTO(dto) }));
      } catch (err) {
        console.error('Erro ao parsear evento expedicao:', err);
      }
    });

    return () => source.close();
  }, []);

  return <MonitorContext.Provider value={state}>{children}</MonitorContext.Provider>;
}

export function useMonitorContext() {
  const ctx = useContext(MonitorContext);
  if (!ctx) {
    throw new Error('useMonitorContext deve ser usado dentro de um MonitorProvider');
  }
  return ctx;
}
