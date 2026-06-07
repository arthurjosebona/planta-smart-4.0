import React from 'react';
import { StatusBadge } from '@components/atoms/StatusBadge';
import { ActionButton } from '@components/atoms/ActionButton';
import { StatusPedido } from '@enums/StatusPedido';

interface PedidoCardHeaderProps {
  id: number;
  ordemDeProducao: number;
  status: StatusPedido;
  iniciarProducao?: () => void;
}

export const PedidoCardHeader: React.FC<PedidoCardHeaderProps> = ({
  id,
  ordemDeProducao,
  status,
  iniciarProducao,
}) => {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        flexWrap: 'wrap',
        gap: 8,
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap' }}>
        <span
          style={{
            fontFamily: 'var(--font-mono)',
            fontSize: 11,
            color: 'var(--color-muted)',
            background: 'var(--color-bg-2)',
            border: '1px solid var(--color-border)',
            borderRadius: 'var(--radius-sm)',
            padding: '2px 7px',
          }}
        >
          #{id}
        </span>
        <span
          style={{
            fontFamily: 'var(--font-mono)',
            fontSize: 14,
            fontWeight: 600,
            color: 'var(--color-text)',
            letterSpacing: '0.02em',
          }}
        >
          {ordemDeProducao}
        </span>
        <StatusBadge status={status} />
      </div>

      <div style={{ display: 'flex', gap: 6 }}>
        <ActionButton label="IniciarProdução" onClick={iniciarProducao} />
      </div>
    </div>
  );
};