import styles from './StatusIndicator.module.css';

interface StatusIndicatorProps {
  conectado: boolean;
  /** Versão compacta (sem pino/rotação/sombra de papel), para barras de navegação. */
  compact?: boolean;
}

export function StatusIndicator({ conectado, compact = false }: StatusIndicatorProps) {
  return (
    <div
      className={`${styles.sticker} ${conectado ? styles.online : styles.offline} ${
        compact ? styles.compact : ''
      }`}
      role="status"
      aria-live="polite"
      title={conectado ? 'CLP conectado' : 'CLP desconectado'}
    >
      {!compact && <span className={styles.pin} aria-hidden="true" />}

      <span className={styles.led} aria-hidden="true">
        <span className={styles.ledRing} />
        <span className={styles.ledCore} />
      </span>

      <span className={styles.text}>
        <span className={styles.label}>CLP</span>
        <span className={styles.state}>
          {conectado ? 'Online' : 'Offline'}
        </span>
      </span>
    </div>
  );
}