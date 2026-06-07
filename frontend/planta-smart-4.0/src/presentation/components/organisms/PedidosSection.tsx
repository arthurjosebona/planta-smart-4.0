import React from 'react';
import { PedidoCard } from '@components/organisms/PedidoCard';
import { ActionButton } from '@components/atoms/ActionButton';
import { Pedido } from '@entities/Pedido';

interface PedidosSectionProps {
  pedidos: Pedido[];
  loading: boolean;
  iniciarProducao?: () => void;
}

export const PedidosSection: React.FC<PedidosSectionProps> = ({
  pedidos,
  loading,
  iniciarProducao,
}) => {
  return (
    <section
      style={{
        background: 'var(--color-surface)',
        border: '1px solid var(--color-border)',
        padding: 24,
        borderRadius: 'var(--radius-md)',
        position: 'relative',
      }}
      aria-label="Pedidos de Produção"
    >
      {/* accent line topo da section */}
      <div
        aria-hidden="true"
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          height: 1,
          background: 'linear-gradient(90deg, transparent, var(--color-accent), transparent)',
          opacity: 0.4,
          borderRadius: 'var(--radius-md) var(--radius-md) 0 0',
        }}
      />

      <h1
        style={{
          fontSize: 22,
          fontWeight: 700,
          letterSpacing: '-0.02em',
          marginBottom: 20,
          color: 'var(--color-text)',
          fontFamily: 'var(--font-sans)',
        }}
      >
        Pedidos
      </h1>

      {loading && pedidos.length === 0 ? (
        <p
          style={{
            color: 'var(--color-muted-2)',
            fontStyle: 'italic',
            fontSize: 13,
            padding: '24px 0',
            textAlign: 'center',
            fontFamily: 'var(--font-mono)',
          }}
        >
          Carregando…
        </p>
      ) : pedidos.length === 0 ? (
        <p
          style={{
            color: 'var(--color-muted-2)',
            fontStyle: 'italic',
            fontSize: 13,
            padding: '24px 0',
            textAlign: 'center',
            fontFamily: 'var(--font-mono)',
            border: '1px solid var(--color-border)',
            borderRadius: 'var(--radius-sm)',
          }}
        >
          Nenhum pedido encontrado.
        </p>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 20 }}>
          {pedidos.map((p) => (
            <PedidoCard key={p.id} pedido={p} iniciarProducao={iniciarProducao} />
          ))}
        </div>
      )}
    </section>
  );
};