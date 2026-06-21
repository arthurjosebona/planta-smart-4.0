import type { ClpStatus } from '@entities/ClpStream';
import styles from './clpStatusChip.module.css';

interface ClpStatusChipProps {
  status: ClpStatus;
}

const LABELS: Record<ClpStatus, string> = {
  OCUPADO: 'Ocupado',
  AGUARDANDO: 'Aguardando',
  MANUAL: 'Manual',
  EMERGENCIA: 'Emergência',
};

export function ClpStatusChip({ status }: ClpStatusChipProps) {
  return (
    <span className={`${styles.chip} ${styles[status.toLowerCase() as Lowercase<ClpStatus>]}`}>
      {LABELS[status]}
    </span>
  );
}
