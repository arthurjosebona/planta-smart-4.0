import React from 'react';
import { FieldDisplay } from '@components/atoms/FieldDisplay/FieldDisplay';
import { Bloco } from '@entities/Bloco';
import styles from './BlocoResumo.module.css';

interface BlocoResumoProps {
  bloco: Bloco;
  index: number;
}

function capitalize(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export function BlocoResumo({ bloco, index }: BlocoResumoProps) {
  return (
    <div className={styles.card}>

      <span className={styles.cardTitle}>
        Bloco {index + 1}
      </span>

      <div className={styles.fieldsGrid}>
        <FieldDisplay label="Cor">{capitalize(bloco.cor)}</FieldDisplay>
      </div>

      {bloco.laminas.length > 0 && (
        <div className={styles.laminasList}>
          {bloco.laminas.map((lamina, i) => (
            <div key={lamina.id ?? i} className={styles.laminaRow}>

              <div className={styles.laminaTitle}>
                <span className={styles.laminaTitleText}>
                  Lâmina {capitalize(lamina.posicao)}
                </span>
              </div>

              <FieldDisplay label="Cor">{capitalize(lamina.cor)}</FieldDisplay>
              <FieldDisplay label="Padrão">{capitalize(lamina.padrao)}</FieldDisplay>

            </div>
          ))}
        </div>
      )}

    </div>
  );
}