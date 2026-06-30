import styles from '@components/atoms/ProducaoTimer/producaoTimer.module.css';

interface ProducaoTimerProps {
  segundos: number;
}

function formatar(segundos: number): string {
  const s = Math.max(0, Math.floor(segundos));
  const hh = Math.floor(s / 3600);
  const mm = Math.floor((s % 3600) / 60);
  const ss = s % 60;
  const pad = (n: number) => n.toString().padStart(2, '0');
  return hh > 0 ? `${pad(hh)}:${pad(mm)}:${pad(ss)}` : `${pad(mm)}:${pad(ss)}`;
}

export function ProducaoTimer({ segundos }: ProducaoTimerProps) {
  return (
    <time className={styles.timer} aria-label="Tempo de execução" role="timer">
      <span aria-hidden="true" className={styles.dot} />
      {formatar(segundos)}
    </time>
  );
}
