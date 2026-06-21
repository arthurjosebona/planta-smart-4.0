import { StatusBadge } from '@components/atoms/StatusBadge/StatusBadge';
import { CorTampaDot } from '@components/atoms/CorTampaDot/CorTampaDot';
import { Pedido } from '@entities/Pedido';
import { TipoPedido } from '@enums/TipoPedido';
import styles from '@components/organisms/PedidoCard/pedidoCard.module.css';

interface PedidoCardProps {
  pedido: Pedido;
  onClick: () => void;
}

function capitalize(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export function PedidoCard({ pedido, onClick }: PedidoCardProps) {
  return (
    <article
      className={styles.card}
      onClick={onClick}
      role="button"
      tabIndex={0}
      onKeyDown={(e) => {
        if (e.key === 'Enter' || e.key === ' ') onClick();
      }}
    >
      <div aria-hidden="true" className={styles.accentLine} />

      <div className={styles.summary}>
        <span className={styles.idTag}>#{pedido.id}</span>
        <span className={styles.ordemDeProducao}>{pedido.ordemDeProducao}</span>
        <span className={styles.tipo}>{capitalize(TipoPedido[pedido.tipo])}</span>
        <CorTampaDot cor={pedido.corTampa} />
        <span className={styles.blocosCount}>{pedido.blocos.length} bloco(s)</span>
        <StatusBadge status={pedido.status} />
      </div>
    </article>
  );
}
