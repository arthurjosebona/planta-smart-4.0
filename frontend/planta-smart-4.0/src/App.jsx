import React, { useState } from 'react';
import { BlockConfigurator } from './components/BlockConfigurator';

export default function App() {
  const [config, setConfig] = useState(null);

  return (
    <div
      style={{
        width: '100vw',
        height: '100vh',
        padding: 20,
        boxSizing: 'border-box',
        background: '#f5f5f5',
      }}
    >
      <h1>Configurador 3D de Blocos</h1>

      <BlockConfigurator
        onChange={(state) => {
          setConfig(state);
          console.log('Nova configuração:', state);
        }}
      />

      <pre
        style={{
          marginTop: 20,
          padding: 12,
          background: '#222',
          color: '#0f0',
          borderRadius: 8,
          overflow: 'auto',
        }}
      >
        {JSON.stringify(config, null, 2)}
      </pre>
    </div>
  );
}
