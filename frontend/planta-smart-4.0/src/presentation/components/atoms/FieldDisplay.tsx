import React from 'react';

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
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
      <span
        style={{
          fontFamily: 'var(--font-mono)',
          fontSize: 10,
          letterSpacing: '0.1em',
          textTransform: 'uppercase',
          color: 'var(--color-muted)',
          fontWeight: 500,
        }}
      >
        {label}
      </span>
      <span
        style={{
          fontFamily: 'var(--font-mono)',
          fontSize: 12,
          color: empty
            ? 'var(--color-muted)'
            : highlight
              ? 'var(--color-text)'
              : 'var(--color-text-dim)',
          fontWeight: highlight ? 500 : 400,
        }}
      >
        {children}
      </span>
    </div>
  );
}
