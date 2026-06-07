import React from 'react';
import { StatusPedido } from '@enums/StatusPedido';

interface StatusBadgeProps {
  status: StatusPedido;
}

function capitalize(str: string): string {
  const s = str.toLowerCase().replace(/_/g, ' ');
  return s.charAt(0).toUpperCase() + s.slice(1);
}

export function StatusBadge({ status }: StatusBadgeProps) {
  const key = status;
  return (
    <span
      style={{
        display: 'inline-block',
        padding: '3px 9px',
        fontSize: 11,
        fontFamily: 'var(--font-mono)',
        letterSpacing: '0.06em',
        borderRadius: 'var(--radius-sm)',
        border: '1px solid transparent',
        fontWeight: 500,
        ...badgeStyles[key],
      }}
    >
      {capitalize(StatusPedido[status])}
    </span>
  );
}

const badgeStyles: Record<string, React.CSSProperties> = {
  pendente: {
    background: 'rgba(250,177,34,0.12)',
    borderColor: 'rgba(250,177,34,0.3)',
    color: '#fab122',
  },
  em_producao: {
    background: 'rgba(91,142,248,0.12)',
    borderColor: 'rgba(91,142,248,0.3)',
    color: '#5b8ef8',
  },
  concluido: {
    background: 'rgba(52,211,153,0.12)',
    borderColor: 'rgba(52,211,153,0.3)',
    color: '#34d399',
  },
  cancelado: {
    background: 'rgba(248,91,91,0.12)',
    borderColor: 'rgba(248,91,91,0.3)',
    color: '#f85b5b',
  },
};
