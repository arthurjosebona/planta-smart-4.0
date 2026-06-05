import React from 'react';
import type {
  ConfiguradorState,
  ConfigBloco,
  CorBloco,
  CorLamina,
  CorTampa,
  Padrao,
  Face,
} from '../types/bloco';

interface BlockFormProps {
  state: ConfiguradorState;
  onChange: (state: ConfiguradorState) => void;
}

// ─── Color palettes ───────────────────────────────────────────────────────────

const COR_BLOCO: Record<CorBloco, string> = {
  preto: '#1a1a1a',
  vermelho: '#cc2222',
  azul: '#1a55cc',
};

const COR_LAMINA: Record<CorLamina, string> = {
  vermelho: '#cc2222',
  azul: '#1a55cc',
  amarelo: '#e6b800',
  verde: '#229944',
  preto: '#1a1a1a',
  branco: '#f0f0ee',
};

const COR_TAMPA: Record<CorTampa, string> = {
  preto: '#1a1a1a',
  vermelho: '#cc2222',
  azul: '#1a55cc',
};

const PADROES: Padrao[] = ['casa', 'estrela', 'navio'];
const FACES: Face[] = ['frente', 'esquerda', 'direita'];
const FACE_LABELS: Record<Face, string> = {
  frente: 'Frente',
  esquerda: 'Esquerda',
  direita: 'Direita',
};

// ─── Helpers ──────────────────────────────────────────────────────────────────

function makeDefaultBloco(cor: CorBloco = 'azul'): ConfigBloco {
  return {
    cor,
    laminas: {
      frente: { cor: null, padrao: null },
      esquerda: { cor: null, padrao: null },
      direita: { cor: null, padrao: null },
    },
  };
}

// ─── Sub-components ──────────────────────────────────────────────────────────

function ColorSwatch({
  color,
  selected,
  onClick,
  title,
  size = 28,
}: {
  color: string;
  selected: boolean;
  onClick: () => void;
  title: string;
  size?: number;
}) {
  return (
    <button
      title={title}
      onClick={onClick}
      style={{
        width: size,
        height: size,
        borderRadius: 4,
        border: selected ? '2px solid #333' : '2px solid transparent',
        outline: selected ? '2px solid #fff' : 'none',
        outlineOffset: -3,
        background: color,
        cursor: 'pointer',
        padding: 0,
        flexShrink: 0,
        boxShadow: selected ? '0 0 0 3px #333' : '0 1px 3px rgba(0,0,0,0.25)',
        transition: 'box-shadow 0.1s',
      }}
    />
  );
}

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div style={{ marginBottom: 16 }}>
      <div
        style={{
          fontSize: 11,
          fontWeight: 700,
          textTransform: 'uppercase',
          letterSpacing: '0.08em',
          color: '#888',
          marginBottom: 8,
        }}
      >
        {title}
      </div>
      {children}
    </div>
  );
}

function Divider() {
  return <hr style={{ border: 'none', borderTop: '1px solid #e8e8e8', margin: '16px 0' }} />;
}

// ─── Main component ───────────────────────────────────────────────────────────

export function BlockForm({ state, onChange }: BlockFormProps) {
  // ── helpers that produce a new state immutably ──────────────────────────────

  function setNumBlocos(n: 1 | 2 | 3) {
    onChange({ ...state, numBlocos: n });
  }

  function setCorTampa(cor: CorTampa) {
    onChange({ ...state, corTampa: cor });
  }

  function setBlocoField(idx: number, updates: Partial<ConfigBloco>) {
    const blocos = [...state.blocos] as typeof state.blocos;
    blocos[idx] = { ...blocos[idx], ...updates };
    onChange({ ...state, blocos });
  }

  function setBlocoColor(idx: number, cor: CorBloco) {
    setBlocoField(idx, { cor });
  }

  function setLaminaCor(idx: number, face: Face, cor: CorLamina | null) {
    const blocos = [...state.blocos] as typeof state.blocos;
    const laminas = { ...blocos[idx].laminas };
    laminas[face] = { cor, padrao: cor === null ? null : laminas[face].padrao };
    blocos[idx] = { ...blocos[idx], laminas };
    onChange({ ...state, blocos });
  }

  function setLaminaPadrao(idx: number, face: Face, padrao: Padrao | null) {
    const blocos = [...state.blocos] as typeof state.blocos;
    const laminas = { ...blocos[idx].laminas };
    laminas[face] = { ...laminas[face], padrao };
    blocos[idx] = { ...blocos[idx], laminas };
    onChange({ ...state, blocos });
  }

  // ── summary ────────────────────────────────────────────────────────────────

  function buildSummary() {
    const lines: string[] = [];
    lines.push(`${state.numBlocos} bloco(s) · tampa ${state.corTampa}`);
    for (let i = 0; i < state.numBlocos; i++) {
      const b = state.blocos[i];
      const laminaTexts = FACES.map((f) => {
        const l = b.laminas[f];
        if (!l.cor) return null;
        return `${FACE_LABELS[f].toLowerCase()}: ${l.cor}${l.padrao ? ` (${l.padrao})` : ''}`;
      }).filter(Boolean);
      lines.push(
        `Bloco ${i + 1}: ${b.cor}${laminaTexts.length ? ' · ' + laminaTexts.join(', ') : ''}`
      );
    }
    return lines;
  }

  // ── render ─────────────────────────────────────────────────────────────────

  return (
    <div
      style={{
        width: 280,
        minWidth: 280,
        height: '100%',
        overflowY: 'auto',
        padding: '16px 14px',
        boxSizing: 'border-box',
        background: '#fafafa',
        borderRight: '1px solid #e4e4e4',
        fontFamily: 'system-ui, sans-serif',
        fontSize: 13,
        color: '#222',
      }}
    >
      {/* ── Número de blocos ── */}
      <Section title="Número de blocos">
        <div style={{ display: 'flex', gap: 8 }}>
          {([1, 2, 3] as const).map((n) => (
            <button
              key={n}
              onClick={() => setNumBlocos(n)}
              style={{
                flex: 1,
                padding: '6px 0',
                borderRadius: 6,
                border: state.numBlocos === n ? '2px solid #333' : '1px solid #ccc',
                background: state.numBlocos === n ? '#222' : '#fff',
                color: state.numBlocos === n ? '#fff' : '#444',
                fontWeight: state.numBlocos === n ? 700 : 400,
                cursor: 'pointer',
                fontSize: 14,
              }}
            >
              {n}
            </button>
          ))}
        </div>
      </Section>

      <Divider />

      {/* ── Cor da tampa ── */}
      <Section title="Cor da tampa">
        <div style={{ display: 'flex', gap: 8 }}>
          {(Object.keys(COR_TAMPA) as CorTampa[]).map((cor) => (
            <ColorSwatch
              key={cor}
              color={COR_TAMPA[cor]}
              selected={state.corTampa === cor}
              onClick={() => setCorTampa(cor)}
              title={cor}
              size={30}
            />
          ))}
        </div>
      </Section>

      <Divider />

      {/* ── Configuração de cada bloco ── */}
      {Array.from({ length: state.numBlocos }, (_, i) => {
        const bloco = state.blocos[i];
        return (
          <div key={i}>
            {i > 0 && <Divider />}
            <Section title={`Bloco ${i + 1}`}>
              {/* Cor do bloco */}
              <div style={{ marginBottom: 12 }}>
                <div style={{ fontSize: 12, color: '#666', marginBottom: 6 }}>Cor do bloco</div>
                <div style={{ display: 'flex', gap: 8 }}>
                  {(Object.keys(COR_BLOCO) as CorBloco[]).map((cor) => (
                    <ColorSwatch
                      key={cor}
                      color={COR_BLOCO[cor]}
                      selected={bloco.cor === cor}
                      onClick={() => setBlocoColor(i, cor)}
                      title={cor}
                      size={30}
                    />
                  ))}
                </div>
              </div>

              {/* Lâminas */}
              {FACES.map((face) => {
                const lamina = bloco.laminas[face];
                return (
                  <div
                    key={face}
                    style={{
                      marginBottom: 10,
                      padding: '10px 10px 8px',
                      background: '#f0f0f0',
                      borderRadius: 8,
                    }}
                  >
                    <div style={{ fontSize: 12, fontWeight: 600, color: '#555', marginBottom: 6 }}>
                      Lâmina — {FACE_LABELS[face]}
                    </div>

                    {/* Cor da lâmina */}
                    <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginBottom: 6 }}>
                      {/* botão "sem cor" */}
                      <button
                        onClick={() => setLaminaCor(i, face, null)}
                        title="Sem lâmina"
                        style={{
                          width: 26,
                          height: 26,
                          borderRadius: 4,
                          border: lamina.cor === null ? '2px solid #333' : '1px solid #bbb',
                          background: '#fff',
                          cursor: 'pointer',
                          fontSize: 13,
                          padding: 0,
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          color: '#aaa',
                          flexShrink: 0,
                        }}
                      >
                        ✕
                      </button>
                      {(Object.keys(COR_LAMINA) as CorLamina[]).map((cor) => (
                        <ColorSwatch
                          key={cor}
                          color={COR_LAMINA[cor]}
                          selected={lamina.cor === cor}
                          onClick={() => setLaminaCor(i, face, cor)}
                          title={cor}
                          size={26}
                        />
                      ))}
                    </div>

                    {/* Padrão — apenas se houver cor */}
                    {lamina.cor !== null && (
                      <div>
                        <div style={{ fontSize: 11, color: '#777', marginBottom: 4 }}>Padrão</div>
                        <div style={{ display: 'flex', gap: 6 }}>
                          {/* sem padrão */}
                          <button
                            onClick={() => setLaminaPadrao(i, face, null)}
                            style={{
                              padding: '3px 8px',
                              fontSize: 11,
                              borderRadius: 4,
                              border: lamina.padrao === null ? '2px solid #333' : '1px solid #bbb',
                              background: lamina.padrao === null ? '#333' : '#fff',
                              color: lamina.padrao === null ? '#fff' : '#555',
                              cursor: 'pointer',
                            }}
                          >
                            —
                          </button>
                          {PADROES.map((p) => (
                            <button
                              key={p}
                              onClick={() => setLaminaPadrao(i, face, p)}
                              style={{
                                padding: '3px 8px',
                                fontSize: 11,
                                borderRadius: 4,
                                border: lamina.padrao === p ? '2px solid #333' : '1px solid #bbb',
                                background: lamina.padrao === p ? '#333' : '#fff',
                                color: lamina.padrao === p ? '#fff' : '#555',
                                cursor: 'pointer',
                                textTransform: 'capitalize',
                              }}
                            >
                              {p}
                            </button>
                          ))}
                        </div>
                      </div>
                    )}
                  </div>
                );
              })}
            </Section>
          </div>
        );
      })}

      <Divider />

      {/* ── Resumo ── */}
      <Section title="Resumo">
        <div
          style={{
            background: '#efefef',
            borderRadius: 8,
            padding: '10px 12px',
            fontSize: 12,
            lineHeight: 1.7,
            color: '#444',
          }}
        >
          {buildSummary().map((line, i) => (
            <div key={i}>{line}</div>
          ))}
        </div>
      </Section>
    </div>
  );
}

export { makeDefaultBloco };
