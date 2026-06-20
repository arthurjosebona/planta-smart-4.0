import { useSyncExternalStore } from 'react';
import { conexaoService } from '@config/diContainer';

/**
 * Lê o status de conexão do CLP a partir do ConexaoService (fonte única de verdade).
 * Qualquer componente que use este hook é automaticamente re-renderizado
 * quando o status mudar — não importa onde o conectar()/desconectar() foi disparado.
 */
export function useConexaoStatus(): boolean {
  return useSyncExternalStore(conexaoService.subscribe, conexaoService.getSnapshot);
}