import React from 'react';
import styles from '@components/atoms/FieldDisplay/fieldDisplay.module.css';

interface FieldDisplayProps {
  label: string;
  children: React.ReactNode;
  highlight?: boolean;
  empty?: boolean;
}

export function FieldDisplay({
  label,
  children,
  highlight = false,
  empty = false,
}: FieldDisplayProps) {
  const valueClass = [
    styles.value,
    empty ? styles.valueEmpty : highlight ? styles.valueHighlight : '',
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <div className={styles.field}>
      <span className={styles.label}>{label}</span>
      <span className={valueClass}>{children}</span>
    </div>
  );
}
