import styles from './clpValueField.module.css';

interface ClpValueFieldProps {
  label: string;
  value: number | number[] | string;
}

export function ClpValueField({ label, value }: ClpValueFieldProps) {
  const display = Array.isArray(value) ? `[${value.join(', ')}]` : String(value);

  return (
    <div className={styles.field}>
      <span className={styles.label}>{label}</span>
      <span className={styles.value}>{display}</span>
    </div>
  );
}
