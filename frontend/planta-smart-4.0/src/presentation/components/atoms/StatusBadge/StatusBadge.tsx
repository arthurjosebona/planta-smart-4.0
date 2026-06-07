import React from 'react';
import { StatusPedido } from '@enums/StatusPedido';
import styles from '@components/atoms/StatusBadge/statusBadge.module.css';

interface StatusBadgeProps {
  status: StatusPedido;
}

function capitalize(str: string): string {
  const s = str.toLowerCase().replace(/_/g, ' ');
  return s.charAt(0).toUpperCase() + s.slice(1);
}

export function StatusBadge({ status }: StatusBadgeProps) {
  return (
    <span className={`${styles.badge} ${styles[status] ?? ''}`}>
      {capitalize(StatusPedido[status])}
    </span>
  );
}
