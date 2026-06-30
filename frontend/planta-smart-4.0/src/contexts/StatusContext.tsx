import { createContext, useContext, useSyncExternalStore, ReactNode } from 'react';
import { conexaoService } from '@config/diContainer';

interface StatusContextValue {
  conectado: boolean;
}

const StatusContext = createContext<StatusContextValue | null>(null);

export function StatusProvider({ children }: { children: ReactNode }) {
  const conectado = useSyncExternalStore(
    conexaoService.subscribe,
    conexaoService.getConectado,
  );

  return (
    <StatusContext.Provider value={{ conectado }}>
      {children}
    </StatusContext.Provider>
  );
}

export function useStatusContext() {
  const ctx = useContext(StatusContext);
  if (!ctx) {
    throw new Error('useStatusContext deve ser usado dentro de um StatusProvider');
  }
  return ctx;
}