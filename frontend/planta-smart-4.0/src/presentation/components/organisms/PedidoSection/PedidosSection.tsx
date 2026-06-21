import React, { useState } from 'react';
import { PedidoCard } from '@components/organisms/PedidoCard/PedidoCard';
import { PedidoModal } from '@components/organisms/PedidoModal/PedidoModal';
import { Pedido } from '@entities/Pedido';
import styles from '@components/organisms/PedidoSection/pedidosSection.module.css';
import { StatusPedido } from '@enums/StatusPedido';

interface PedidosSectionProps {
  pedidos: Pedido[];
  loading: boolean;
  filtroStatus: StatusPedido | null;
  onFiltroStatus: (tipo: StatusPedido | null) => void;
  iniciarProducao: (id: number) => void;
  deletarPedido: (id: number) => void;
}

function capitalize(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

// Todos os valores numéricos do enum (filtra as keys string do TS)
const STATUS = Object.values(StatusPedido).filter(
  (v): v is StatusPedido => typeof v === 'number',
);

export function PedidosSection({
  pedidos,
  loading,
  filtroStatus,
  onFiltroStatus,
  iniciarProducao,
  deletarPedido,
}: PedidosSectionProps) {
  const [pedidoSelecionadoId, setPedidoSelecionadoId] = useState<number | null>(null);
  const pedidoSelecionado = pedidos.find((p) => p.id === pedidoSelecionadoId) ?? null;

  return (
    <section className={styles.section} aria-label="Pedidos de Produção">
      <div aria-hidden="true" className={styles.accentLine} />

      <div className={styles.sectionHeader}>
        <h1 className={styles.heading}>Pedidos</h1>

        <div className={styles.filterBar} role="group" aria-label="Filtrar por tipo">
          <button
            className={`${styles.filterBtn} ${filtroStatus === null ? styles.filterBtnActive : ''}`}
            onClick={() => onFiltroStatus(null)}
          >
            All
          </button>
          {STATUS.map((status) => (
            <button
              key={status}
              className={`${styles.filterBtn} ${filtroStatus === status ? styles.filterBtnActive : ''}`}
              onClick={() => onFiltroStatus(status)}
            >
              {capitalize(StatusPedido[status])}
            </button>
          ))}
        </div>
      </div>

      {loading && pedidos.length === 0 ? (
        <p className={styles.stateMsg}>Carregando…</p>
      ) : pedidos.length === 0 ? (
        <p className={`${styles.stateMsg} ${styles.stateMsgEmpty}`}>Nenhum pedido encontrado.</p>
      ) : (
        <div className={styles.list}>
          {pedidos.map((p) => (
            <PedidoCard key={p.id} pedido={p} onClick={() => setPedidoSelecionadoId(p.id)} />
          ))}
        </div>
      )}

      {pedidoSelecionado && (
        <PedidoModal
          pedido={pedidoSelecionado}
          iniciarProducao={iniciarProducao}
          deletarPedido={(id) => {
            deletarPedido(id);
            setPedidoSelecionadoId(null);
          }}
          onClose={() => setPedidoSelecionadoId(null)}
        />
      )}
    </section>
  );
};