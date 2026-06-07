import React from 'react';
import { CorTampa } from '@enums/CorTampa';
import styles from './CorTampaDot.module.css';

interface CorTampaDotProps {
  cor: CorTampa;
}

const corMap: Record<string, string> = {
  PRETO: '#e8e8f0',
  AZUL: '#5b8ef8',
  BRANCO: '#e8e8f0',
  VAZIO: '#2a2a32',
};

function capitalize(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export function CorTampaDot({ cor }: CorTampaDotProps) {
  return (
    <span className={styles.wrapper}>
      <span
        className={styles.dot}
        style={{ background: corMap[cor] ?? '#2a2a32' }}
      />
      {capitalize(cor)}
    </span>
  );
}