import { StatusBadge } from '@components/atoms/StatusBadge/StatusBadge';
import { CorTampaDot } from '@components/atoms/CorTampaDot/CorTampaDot';
import { Pedido } from '@entities/Pedido';
import { TipoPedido } from '@enums/TipoPedido';
import styles from '@components/organisms/PedidoCard/pedidoCard.module.css';

interface PedidoCardProps {
  pedido: Pedido;
  iniciarProducao: (id: number) => void;
  onAtualizar: (id: number) => void;
  onDeletar: (id: number) => void;
}

export function PedidoCard({ pedido, iniciarProducao, onAtualizar, onDeletar }: PedidoCardProps) {
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

      <PedidoCardHeader
        id={pedido.id!}
        ordemDeProducao={pedido.ordemDeProducao}
        status={pedido.status}
        iniciarProducao={iniciarProducao}
        onAtualizar={onAtualizar}
        onDeletar={onDeletar}
      />

      <hr className={styles.divider} />

      <PedidoCardFields
        tipo={pedido.tipo}
        corTampa={pedido.corTampa}
        blocos={pedido.blocos}
        expedicaoId={pedido.expedicao?.id ?? null}
        registroCriacao={pedido.registroCriacao!}
        registroEntradaExpedicao={pedido.registroEntradaExpedicao}
        registroSaidaExpedicao={pedido.registroSaidaExpedicao}
      />
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