import { useEffect, useRef, useState } from 'react';
import { Pedido } from '@entities/Pedido';
import { pedidoService } from '@config/diContainer';
import { PedidoModal } from '@components/organisms/PedidoModal/PedidoModal';
import styles from './opEmCursoCard.module.css';

interface OpEmCursoCardProps {
  numeroOP: number;
  pedidoEmCurso: boolean;
}

function formatElapsed(seconds: number): string {
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = seconds % 60;
  if (h > 0) {
    return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  }
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
}

export function OpEmCursoCard({ numeroOP, pedidoEmCurso }: OpEmCursoCardProps) {
  const [elapsed, setElapsed] = useState(0);
  const startRef = useRef<number>(Date.now());
  const prevOPRef = useRef<number>(numeroOP);

  const [pedidoSelecionado, setPedidoSelecionado] = useState<Pedido | null>(null);
  const [loadingPedido, setLoadingPedido] = useState(false);

  // Reinicia o timer quando o OP muda
  useEffect(() => {
    if (numeroOP !== prevOPRef.current) {
      prevOPRef.current = numeroOP;
      startRef.current = Date.now();
      setElapsed(0);
    }
  }, [numeroOP]);

  // Atualiza o elapsed a cada segundo enquanto há pedido em curso
  useEffect(() => {
    if (!pedidoEmCurso) {
      setElapsed(0);
      return;
    }
    startRef.current = Date.now() - elapsed * 1000;
    const timer = setInterval(() => {
      setElapsed(Math.floor((Date.now() - startRef.current) / 1000));
    }, 1000);
    return () => clearInterval(timer);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [pedidoEmCurso]);

  async function handleClick() {
    setLoadingPedido(true);
    try {
      const pedido = await pedidoService.findByOrdemDeProducao(numeroOP);
      setPedidoSelecionado(pedido);
    } finally {
      setLoadingPedido(false);
    }
  }

  if (!pedidoEmCurso) return null;

  return (
    <>
      <button
        type="button"
        className={styles.card}
        onClick={handleClick}
        disabled={loadingPedido}
        aria-label={`Pedido OP-${numeroOP} em produção`}
      >
        <span className={styles.dot} aria-hidden="true" />
        <span className={styles.label}>Em produção</span>
        <span className={styles.op}>OP-{numeroOP}</span>
        <span className={styles.sep} aria-hidden="true">|</span>
        <span className={styles.elapsed}>{formatElapsed(elapsed)}</span>
        {loadingPedido && <span className={styles.loading}>…</span>}
      </button>

      {pedidoSelecionado && (
        <PedidoModal
          pedido={pedidoSelecionado}
          iniciarProducao={async (id) => {
            await pedidoService.iniciarProducao(id);
          }}
          deletarPedido={async (id) => {
            await pedidoService.delete(id);
            setPedidoSelecionado(null);
          }}
          onClose={() => setPedidoSelecionado(null)}
          conectado={false} // passa false pq não será possível mandar em produção se já está em produção
        />
      )}
    </>
  );
}
