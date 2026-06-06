import React from 'react';
import { StoreModel } from '@pages/Store/StoreModel';
import { CorTampa } from '@enums/CorTampa';
import { CorBloco } from '@enums/CorBloco';
import { CorLamina } from '@enums/CorLamina';
import { PadraoLamina } from '@enums/PadraoLamina';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { ConfigBloco } from '@valueObjects/ConfigBloco';
import Divider from '@components/atoms/Divider';
import Section from '@components/molecules/Section';
import ColorSwatch from '@components/atoms/ColorWatch';
import { Pedido } from '@entities/Pedido';

// ─── Color palettes ───────────────────────────────────────────────────────────

const COR_BLOCO: Record<CorBloco, string> = {
  [CorBloco.Preto]: '#1a1a1a',
  [CorBloco.Vermelho]: '#cc2222',
  [CorBloco.Azul]: '#1a55cc',
};

const COR_LAMINA: Record<CorLamina, string> = {
  [CorLamina.Vermelho]: '#cc2222',
  [CorLamina.Azul]: '#1a55cc',
  [CorLamina.Amarelo]: '#e6b800',
  [CorLamina.Verde]: '#229944',
  [CorLamina.Preto]: '#1a1a1a',
  [CorLamina.Branco]: '#f0f0ee',
};

const COR_TAMPA: Record<CorTampa, string> = {
  [CorTampa.Preto]: '#1a1a1a',
  [CorTampa.Vermelho]: '#cc2222',
  [CorTampa.Azul]: '#1a55cc',
};

const FACE_LABELS: Record<PosicaoLamina, string> = {
  [PosicaoLamina.Frente]: 'Frente',
  [PosicaoLamina.Esquerda]: 'Esquerda',
  [PosicaoLamina.Direita]: 'Direita',
};

interface OrderFormProps {
  state: StoreModel;
  setNumBlocos: (n: 1 | 2 | 3) => void;
  setCorTampa: (cor: CorTampa) => void;
  setBlocoField: (idx: number, updates: Partial<ConfigBloco>) => void;
  setBlocoColor: (idx: number, cor: CorBloco) => void;
  setLaminaCor: (idx: number, posicao: PosicaoLamina, cor: CorLamina | null) => void;
  setLaminaPadrao: (idx: number, posicao: PosicaoLamina, padrao: PadraoLamina | null) => void;
  setOrdemDeProducao: (n: number) => void;
  createPedido: () => void;
}

const FACES = Object.values(PosicaoLamina) as PosicaoLamina[];
const PADROES = Object.values(PadraoLamina) as PadraoLamina[];
const CORES_TAMPA = Object.values(CorTampa) as CorTampa[];
const CORES_BLOCO = Object.values(CorBloco) as CorBloco[];
const CORES_LAMINA = Object.values(CorLamina) as CorLamina[];

export function OrderForm({
  state,
  setNumBlocos,
  setCorTampa,
  setBlocoField,
  setBlocoColor,
  setLaminaCor,
  setLaminaPadrao,
  setOrdemDeProducao,
  createPedido,
}: OrderFormProps) {
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
      {/* ── Ordem de produção ── */}
      <Section title="Ordem de produção">
        <input
          type="number"
          min={1}
          value={state.ordemDeProducao}
          onChange={(e) => setOrdemDeProducao(Math.max(1, Number(e.target.value)))}
          style={{
            width: '100%',
            padding: '6px 10px',
            borderRadius: 6,
            border: '1px solid #ccc',
            fontSize: 14,
            boxSizing: 'border-box',
            outline: 'none',
          }}
        />
      </Section>

      <Divider />


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
          {CORES_TAMPA.map((cor) => (
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
                  {CORES_BLOCO.map((cor) => (
                    <ColorSwatch
                      key={cor}
                      color={COR_BLOCO[cor]}
                      selected={bloco.cor === cor}
                      onClick={() => setBlocoColor(i, cor)}
                      title={cor as string}
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
                      {CORES_LAMINA.map((cor) => (
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

      {/* ── Criar pedido ── */}
      <Divider />
      <div style={{ padding: '8px 0' }}>
        <button
          onClick={createPedido}
          disabled={state.loading}
          style={{
            width: '100%',
            padding: '9px 0',
            borderRadius: 6,
            border: 'none',
            background: state.loading ? '#888' : '#1a55cc',
            color: '#fff',
            fontWeight: 700,
            fontSize: 14,
            cursor: state.loading ? 'not-allowed' : 'pointer',
            transition: 'background 0.2s',
          }}
        >
          {state.loading ? 'Criando...' : 'Criar pedido'}
        </button>
      </div>

      {/* ── Debug panel ── */}
      <Divider />
      <div
        style={{
          background: '#1a1a1a',
          color: '#a0e080',
          borderRadius: 8,
          padding: '10px 12px',
          fontSize: 11,
          fontFamily: 'monospace',
          lineHeight: 1.7,
        }}
      >
        <div style={{ color: '#888', marginBottom: 4, fontSize: 10, textTransform: 'uppercase', letterSpacing: 1 }}>
          debug
        </div>
        <div>
          loading:{' '}
          <span style={{ color: state.loading ? '#f0c040' : '#a0e080' }}>
            {String(state.loading)}
          </span>
        </div>
        <div>
          sucesso:{' '}
          <span style={{ color: state.sucesso ? '#a0e080' : '#888' }}>
            {String(state.sucesso)}
          </span>
        </div>
        <div>
          erro:{' '}
          <span style={{ color: state.erro ? '#ff6b6b' : '#888' }}>
            {state.erro ?? 'null'}
          </span>
        </div>
        <div>
          pedidoCriado:{' '}
          <span style={{ color: state.pedidoCriado ? '#a0e080' : '#888' }}>
            {state.pedidoCriado ? `id ${state.pedidoCriado.id}` : 'null'}
          </span>
        </div>
      </div>
    </div>
  );
}
