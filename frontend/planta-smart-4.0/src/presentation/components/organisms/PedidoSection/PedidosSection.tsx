import React from 'react';
import { PedidoCard } from '@components/organisms/PedidoCard/PedidoCard';
import { Pedido } from '@entities/Pedido';
import styles from '@components/organisms/PedidoSection/pedidosSection.module.css';

interface PedidosSectionProps {
  pedidos: Pedido[];
  loading: boolean;
  iniciarProducao: (id: number) => void;
}

export const PedidosSection: React.FC<PedidosSectionProps> = ({
  pedidos,
  loading,
  iniciarProducao,
}) => {
  return (
    <section className={styles.section} aria-label="Pedidos de Produção">

      <div aria-hidden="true" className={styles.accentLine} />

      <h1 className={styles.heading}>Pedidos</h1>

      {loading && pedidos.length === 0 ? (
        <p className={styles.stateMsg}>Carregando…</p>
      ) : pedidos.length === 0 ? (
        <p className={`${styles.stateMsg} ${styles.stateMsgEmpty}`}>
          Nenhum pedido encontrado.
        </p>
      ) : (
        <div className={styles.list}>
          {pedidos.map((p) => (
            <PedidoCard key={p.id} pedido={p} iniciarProducao={iniciarProducao} />
          ))}
        </div>
      )}

    </section>
  );
};