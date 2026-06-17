import { PedidoCardHeader } from '@components/molecules/PedidoCardHeader/PedidoCardHeader';
import { PedidoCardFields } from '@components/molecules/PedidoCardFields/PedidoCardFields';
import { Pedido } from '@entities/Pedido';
import styles from '@components/organisms/PedidoCard/pedidoCard.module.css';

interface PedidoCardProps {
  pedido: Pedido;
  iniciarProducao: (id: number) => void;
  onAtualizar: (id: number) => void;
  onDeletar: (id: number) => void;
}

export function PedidoCard({ pedido, iniciarProducao, onAtualizar, onDeletar }: PedidoCardProps) {
  return (
    <article className={styles.card}>
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
    </article>
  );
}