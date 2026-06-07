import { PedidoCardHeader } from '@components/molecules/PedidoCardHeader';
import { PedidoCardFields } from '@components/molecules/PedidoCardFields';
import { Pedido } from '@entities/Pedido';

interface PedidoCardProps {
  pedido: Pedido;
  iniciarProducao: (id: number) => void;
}

export function PedidoCard({ pedido, iniciarProducao }: PedidoCardProps) {
  return (
    <article
      style={{
        background: 'var(--color-surface)',
        border: '1px solid var(--color-border)',
        borderRadius: 'var(--radius-md)',
        padding: 16,
        position: 'relative',
        transition: 'border-color 0.15s',
      }}
    >
      {/* linha accent no topo */}
      <div
        aria-hidden="true"
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          height: 1,
          background: 'linear-gradient(90deg, transparent, var(--color-accent), transparent)',
          opacity: 0.35,
          borderRadius: 'var(--radius-md) var(--radius-md) 0 0',
        }}
      />

      <PedidoCardHeader
        id={pedido.id!}
        ordemDeProducao={pedido.ordemDeProducao}
        status={pedido.status}
        iniciarProducao={iniciarProducao}
      />

      <hr
        style={{
          border: 'none',
          borderTop: '1px solid var(--color-border)',
          margin: '12px 0',
        }}
      />

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
