import { useEffect, useState } from 'react';
import type {
  EstoqueStreamDTO,
  ProcessoMontagemStreamDTO,
  ExpedicaoStreamDTO,
} from '@entities/ClpStream';

export interface ClpStreamState {
  estoque: EstoqueStreamDTO | null;
  processo: ProcessoMontagemStreamDTO | null;
  montagem: ProcessoMontagemStreamDTO | null;
  expedicao: ExpedicaoStreamDTO | null;
  conectado: boolean;
}

const SSE_URL = 'http://localhost:8088/api/smart/stream';

export function useClpStream(): ClpStreamState {
  const [state, setState] = useState<ClpStreamState>({
    estoque: null,
    processo: null,
    montagem: null,
    expedicao: null,
    conectado: false,
  });

  useEffect(() => {
    const source = new EventSource(SSE_URL);

    source.onopen = () => setState((s) => ({ ...s, conectado: true }));
    source.onerror = () => setState((s) => ({ ...s, conectado: false }));

    source.addEventListener('estoque', (e) => {
      setState((s) => ({ ...s, estoque: JSON.parse((e as MessageEvent).data) as EstoqueStreamDTO }));
    });
    source.addEventListener('processo', (e) => {
      setState((s) => ({ ...s, processo: JSON.parse((e as MessageEvent).data) as ProcessoMontagemStreamDTO }));
    });
    source.addEventListener('montagem', (e) => {
      setState((s) => ({ ...s, montagem: JSON.parse((e as MessageEvent).data) as ProcessoMontagemStreamDTO }));
    });
    source.addEventListener('expedicao', (e) => {
      setState((s) => ({ ...s, expedicao: JSON.parse((e as MessageEvent).data) as ExpedicaoStreamDTO }));
    });

    return () => source.close();
  }, []);

  return state;
}
