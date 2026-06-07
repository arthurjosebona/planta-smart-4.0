import React from 'react';
import { CorTampa } from '@enums/CorTampa';

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

export const CorTampaDot: React.FC<CorTampaDotProps> = ({ cor }) => {
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5 }}>
      <span
        style={{
          display: 'inline-block',
          width: 10,
          height: 10,
          background: corMap[cor] ?? '#2a2a32',
          border: '1px solid #3a3a46',
          borderRadius: 2,
          flexShrink: 0,
        }}
      />
      {capitalize(cor)}
    </span>
  );
};