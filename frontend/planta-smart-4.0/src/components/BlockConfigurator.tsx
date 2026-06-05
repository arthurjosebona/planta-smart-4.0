import React, { useState, useEffect } from 'react';
import { BlockForm, makeDefaultBloco } from './BlockForm';
import { BlockViewer } from './BlockViewer';
import type { ConfiguradorState } from '../types/bloco';

// ─── Default state factory ─────────────────────────────────────────────────

function makeDefaultState(): ConfiguradorState {
  return {
    numBlocos: 1,
    corTampa: 'preto',
    blocos: [makeDefaultBloco('azul'), makeDefaultBloco('preto'), makeDefaultBloco('vermelho')],
  };
}

// ─── Props ─────────────────────────────────────────────────────────────────

interface BlockConfiguratorProps {
  onChange?: (state: ConfiguradorState) => void;
}

// ─── Component ─────────────────────────────────────────────────────────────

export function BlockConfigurator({ onChange }: BlockConfiguratorProps) {
  const [state, setState] = useState<ConfiguradorState>(makeDefaultState);

  useEffect(() => {
    onChange?.(state);
  }, [state]); // eslint-disable-line react-hooks/exhaustive-deps

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'row',
        width: '100%',
        height: 580,
        overflow: 'hidden',
        borderRadius: 8,
        boxShadow: '0 2px 12px rgba(0,0,0,0.10)',
      }}
    >
      <BlockForm state={state} onChange={setState} />
      <BlockViewer state={state} />
    </div>
  );
}
