import React from 'react';
import { FieldDisplay } from '@components/atoms/FieldDisplay';
import { Bloco } from '@entities/Bloco';
import { AndarBloco } from '@enums/AndarBloco';

interface BlocoResumoProps {
  bloco: Bloco;
  index: number;
}

function capitalize(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export function BlocoResumo({ bloco, index }: BlocoResumoProps) {
  return (
    <div
      style={{
        background: 'var(--color-bg-2)',
        border: '1px solid var(--color-border)',
        borderRadius: 'var(--radius-sm)',
        padding: '10px 12px',
        display: 'flex',
        flexDirection: 'column',
        gap: 8,
      }}
    >
      {/* título do bloco */}
      <span
        style={{
          fontFamily: 'var(--font-mono)',
          fontSize: 10,
          fontWeight: 600,
          letterSpacing: '0.12em',
          textTransform: 'uppercase',
          color: 'var(--color-accent)',
        }}
      >
        Bloco {index + 1}
      </span>

      {/* campos do bloco */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(100px, 1fr))',
          gap: '6px 12px',
        }}
      >
        <FieldDisplay label="Cor">{capitalize(bloco.cor)}</FieldDisplay>
      </div>

      {/* lâminas */}
      {bloco.laminas.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 5, marginTop: 2 }}>
          {bloco.laminas.map((lamina, i) => (
            <div
              key={lamina.id ?? i}
              style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fill, minmax(100px, 1fr))',
                gap: '4px 12px',
                paddingTop: 6,
                borderTop: '1px solid var(--color-border)',
              }}
            >
              {/* título da lâmina */}
              <div style={{ gridColumn: '1 / -1' }}>
                <span
                  style={{
                    fontFamily: 'var(--font-mono)',
                    fontSize: 10,
                    fontWeight: 500,
                    letterSpacing: '0.08em',
                    textTransform: 'uppercase',
                    color: 'var(--color-muted-2)',
                  }}
                >
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
