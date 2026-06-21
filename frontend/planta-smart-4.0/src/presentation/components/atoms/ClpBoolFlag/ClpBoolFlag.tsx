import styles from './clpBoolFlag.module.css';

interface ClpBoolFlagProps {
  label: string;
  value: boolean;
}

export function ClpBoolFlag({ label, value }: ClpBoolFlagProps) {
  return (
    <div className={`${styles.flag} ${value ? styles.active : styles.inactive}`}>
      <span className={styles.dot} aria-hidden="true" />
      <span className={styles.label}>{label}</span>
    </div>
  );
}
