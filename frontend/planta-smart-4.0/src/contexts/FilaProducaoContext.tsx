import { createContext, useContext, useEffect, useRef, useState, ReactNode } from 'react';
import { FilaProducao, FilaProducaoInitial } from '@entities/FilaProducao';
import { FilaStreamDTO } from '@dtos/response/stream/FilaStreamDTO';
import { FilaProducaoMapper } from '@mappers/FilaProducaoMapper';
import { useStatusContext } from './StatusContext';

const SSE_URL = 'http://localhost:8088/api/smart/fila/stream';

interface FilaProducaoContextValue {
  fila: FilaProducao;
  // Tempo de execução em segundos, com tick local de 1s entre os eventos do SSE.
  tempoExecucaoSegundos: number;
  conectado: boolean;
}

const FilaProducaoContext = createContext<FilaProducaoContextValue | null>(null);

export function FilaProducaoProvider({ children }: { children: ReactNode }) {
  const [fila, setFila] = useState<FilaProducao>(FilaProducaoInitial);
  const [tempoExecucaoSegundos, setTempoExecucaoSegundos] = useState(0);
  const { conectado } = useStatusContext();

  // Base do cronômetro: último valor recebido do backend e o instante (monotônico)
  // em que chegou. O tick local extrapola a partir daí, e cada novo evento SSE
  // reancora a base — garantindo que o backend continue sendo a fonte da verdade.
  const baseSegundosRef = useRef(0);
  const baseTimestampRef = useRef(performance.now());
  const emExecucaoRef = useRef(false);
  

  useEffect(() => {

    if (!conectado) {
      return;
    }

    const source = new EventSource(SSE_URL);

    source.addEventListener('fila', (e) => {
      try {
        const dto = JSON.parse((e as MessageEvent).data) as FilaStreamDTO;
        const entidade = FilaProducaoMapper.mapToEntityByStreamDTO(dto);

        emExecucaoRef.current = entidade.emExecucao !== null;
        baseSegundosRef.current = entidade.tempoExecucaoSegundos;
        baseTimestampRef.current = performance.now();

        setFila(entidade);
        setTempoExecucaoSegundos(entidade.tempoExecucaoSegundos);
      } catch (err) {
        console.error('Erro ao parsear evento fila:', err);
      }
    });

    return () => source.close();
  }, [conectado]);

  // Tick local de 1s: avança o cronômetro entre os eventos do SSE sem esperar o
  // próximo tick do servidor. Só corre enquanto há pedido em execução.
  useEffect(() => {
    const intervalo = setInterval(() => {
      if (!emExecucaoRef.current) return;
      const decorrido = (performance.now() - baseTimestampRef.current) / 1000;
      setTempoExecucaoSegundos(Math.floor(baseSegundosRef.current + decorrido));
    }, 1000);

    return () => clearInterval(intervalo);
  }, []);

  return (
    <FilaProducaoContext.Provider value={{ fila, tempoExecucaoSegundos }}>
      {children}
    </FilaProducaoContext.Provider>
  );
}

export function useFilaProducaoContext() {
  const ctx = useContext(FilaProducaoContext);
  if (!ctx) {
    throw new Error('useFilaProducaoContext deve ser usado dentro de um FilaProducaoProvider');
  }
  return ctx;
}
