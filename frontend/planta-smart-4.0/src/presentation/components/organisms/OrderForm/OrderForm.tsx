import React from 'react';
import { StoreModel } from '@pages/Store/StoreModel';
import { CorTampa } from '@enums/CorTampa';
import { CorBloco } from '@enums/CorBloco';
import { CorLamina } from '@enums/CorLamina';
import { PadraoLamina } from '@enums/PadraoLamina';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { ConfigBloco } from '@valueObjects/ConfigBloco';
import Divider from '@components/atoms/Divider/Divider';
import Section from '@components/molecules/Section/Section';
import ColorSwatch from '@components/atoms/ColorSwatch/ColorSwatch';
import styles from '@components/organisms/OrderForm/orderForm.module.css';

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
    <div className={styles.root}>
      {/* ── Ordem de produção ── */}
      <Section title="Ordem de produção">
        <input
          type="number"
          min={1}
          value={state.ordemDeProducao}
          onChange={(e) => setOrdemDeProducao(Math.max(1, Number(e.target.value)))}
          className={styles.input}
        />
      </Section>

      <Divider />

      {/* ── Número de blocos ── */}
      <Section title="Número de blocos">
        <div className={styles.numBlocosGroup}>
          {([1, 2, 3] as const).map((n) => (
            <button
              key={n}
              onClick={() => setNumBlocos(n)}
              className={`${styles.numBlocoBtn} ${state.numBlocos === n ? styles.numBlocoBtnActive : ''}`}
            >
              {n}
            </button>
          ))}
        </div>
      </Section>

      <Divider />

      {/* ── Cor da tampa ── */}
      <Section title="Cor da tampa">
        <div className={styles.swatchRow}>
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
              <div className={styles.corBlocoWrapper}>
                <div className={styles.fieldLabel}>Cor do bloco</div>
                <div className={styles.swatchRow}>
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
                  <div key={face} className={styles.laminaCard}>
                    <div className={styles.laminaTitle}>Lâmina — {FACE_LABELS[face]}</div>

                    {/* Cor da lâmina */}
                    <div className={styles.laminaCorRow}>
                      <button
                        onClick={() => setLaminaCor(i, face, null)}
                        title="Sem lâmina"
                        className={`${styles.laminaSemCor} ${lamina.cor === null ? styles.laminaSemCorActive : ''}`}
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
                        <div className={styles.padraoLabel}>Padrão</div>
                        <div className={styles.padraoGroup}>
                          {PADROES.map((p) => (
                            <button
                              key={p}
                              onClick={() => setLaminaPadrao(i, face, p)}
                              className={`${styles.padraoBtn} ${lamina.padrao === p ? styles.padraoBtnActive : ''}`}
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
      <div className={styles.submitWrapper}>
        <button
          onClick={createPedido}
          disabled={state.loading}
          className={`${styles.submitBtn} ${state.loading ? styles.submitBtnLoading : ''}`}
        >
          {state.loading ? 'Criando...' : 'Criar pedido'}
        </button>
      </div>

      {/* ── Debug panel ── */}
      <Divider />
      <div className={styles.debug}>
        <div className={styles.debugTitle}>debug</div>
        <div>
          loading:{' '}
          <span className={state.loading ? styles.debugLoading : styles.debugOk}>
            {String(state.loading)}
          </span>
        </div>
        <div>
          sucesso:{' '}
          <span className={state.sucesso ? styles.debugOk : styles.debugMuted}>
            {String(state.sucesso)}
          </span>
        </div>
        <div>
          erro:{' '}
          <span className={state.erro ? styles.debugError : styles.debugMuted}>
            {state.erro ?? 'null'}
          </span>
        </div>
        <div>
          pedidoCriado:{' '}
          <span className={state.pedidoCriado ? styles.debugOk : styles.debugMuted}>
            {state.pedidoCriado ? `id ${state.pedidoCriado.id}` : 'null'}
          </span>
        </div>
      </div>
    </div>
  );
}
