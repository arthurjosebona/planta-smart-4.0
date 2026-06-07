import { StatusBadge } from '@components/atoms/StatusBadge/StatusBadge';
import { ActionButton } from '@components/atoms/ActionButton/ActionButton';
import { StatusPedido } from '@enums/StatusPedido';
import styles from '@components/molecules/PedidoCardHeader/pedidoCardHeader.module.css';

interface PedidoCardHeaderProps {
  id: number;
  ordemDeProducao: number;
  status: StatusPedido;
  iniciarProducao: (id: number) => void;
}

export function PedidoCardHeader({
  id,
  ordemDeProducao,
  status,
  iniciarProducao,
}: PedidoCardHeaderProps) {
  return (
    <div className={styles.header}>
      <div className={styles.headerLeft}>
        <span className={styles.idTag}>#{id}</span>
        <span className={styles.ordemDeProducao}>{ordemDeProducao}</span>
        <StatusBadge status={status} />
      </div>

      <div className={styles.actions}>
        {status === StatusPedido.Pendente && (
          <ActionButton label="Iniciar Produção" onClick={() => iniciarProducao(id)} />
        )}
      </div>
    </div>
  );
}
