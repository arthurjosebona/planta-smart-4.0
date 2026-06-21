import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { conexaoService } from '@config/diContainer';

const PING_INTERVAL_MS = 10_000;

export type StationKey = 'estoque' | 'processo' | 'montagem' | 'expedicao';

export type PingMap = Record<StationKey, boolean>;

const PING_DEFAULT: PingMap = {
  estoque: false,
  processo: false,
  montagem: false,
  expedicao: false,
};

interface PingContextValue {
  pingMap: PingMap;
}

const PingContext = createContext<PingContextValue | null>(null);

export function PingProvider({ children }: { children: ReactNode }) {
  const [pingMap, setPingMap] = useState<PingMap>(PING_DEFAULT);

  useEffect(() => {
    async function ping() {
      try {
        const results = await conexaoService.pingAll();
        const next = { ...PING_DEFAULT };
        results.forEach((r) => {
          if (r.nome in next) {
            (next as Record<string, boolean>)[r.nome] = r.online;
          }
        });
        setPingMap(next);
      } catch {
        // mantém o estado anterior se o ping falhar
      }
    }

    ping();
    const timer = setInterval(ping, PING_INTERVAL_MS);
    return () => clearInterval(timer);
  }, []);

  return <PingContext.Provider value={{ pingMap }}>{children}</PingContext.Provider>;
}

export function usePingContext() {
  const ctx = useContext(PingContext);
  if (!ctx) {
    throw new Error('usePingContext deve ser usado dentro de um PingProvider');
  }
  return ctx;
}
