import { useState } from 'react';
import { Pedido } from '@entities/Pedido';
import { pedidoService } from '@config/diContainer';
import { PedidoModal } from '@components/organisms/PedidoModal/PedidoModal';
import styles from './opEmCursoCard.module.css';

interface OpEmCursoCardProps {
  pedido: Pedido | null;
  pedidoEmCurso: boolean;
  tempoDecorrido: string;
}

export function OpEmCursoCard({ pedido, pedidoEmCurso, tempoDecorrido }: OpEmCursoCardProps) {
  const [pedidoSelecionado, setPedidoSelecionado] = useState<Pedido | null>(null);
  const [loadingPedido, setLoadingPedido] = useState(false);

  async function handleClick() {
    setLoadingPedido(true);
    try {
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
        aria-label={`Pedido OP-${pedido?.ordemDeProducao} em produção`}
      >
        <span className={styles.dot} aria-hidden="true" />
        <span className={styles.label}>Em produção</span>
        <span className={styles.op}>OP-{pedido?.ordemDeProducao}</span>
        <span className={styles.sep} aria-hidden="true">|</span>
        <span className={styles.elapsed}>{tempoDecorrido}</span>
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
          conectado={false}
        />
      )}
    </>
  );
}