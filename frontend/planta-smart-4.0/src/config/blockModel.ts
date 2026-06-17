// ─────────────────────────────────────────────────────────────────────────────
// blockModel.ts
// Fonte única de verdade para a geometria e as cores do modelo 3D do bloco
// exibido no viewer da Store. Ajuste qualquer dimensão ou cor por aqui — todos
// os componentes (atoms/molecules/organisms) apenas consomem estas constantes.
//
// Unidades arbitrárias de cena (o bloco tem ~1.7 de largura). Os eixos seguem a
// convenção do three.js: X = largura, Y = altura, Z = profundidade.
// ─────────────────────────────────────────────────────────────────────────────

import { CorBloco } from '@enums/CorBloco';
import { CorLamina } from '@enums/CorLamina';
import { CorTampa } from '@enums/CorTampa';

/** Dimensões gerais de um bloco. */
export const BLOCK = {
  width: 1.7, // X — largura externa
  depth: 1.7, // Z — profundidade externa
  height: 0.82, // Y — altura de um bloco (passo de empilhamento)
  baseThickness: 0.14, // espessura do piso/base (banda sólida inferior)
  cornerRadius: 0.05, // raio de arredondamento padrão das peças
} as const;

/** Borda elevada ao redor do piso (a "moldura" da base). */
export const BASE_RIM = {
  height: 0.07, // o quanto a borda sobe acima do piso
  thickness: 0.09, // espessura da parede da borda
} as const;

/** Colunas verticais nos quatro cantos. */
export const COLUMN = {
  width: 0.19, // seção quadrada (X e Z) — postes esbeltos como no original
  radius: 0.04, // arredondamento
  overshoot: 0.2, // altura extra acima do corpo (cria a sobreposição no empilhamento)
} as const;

/** Pino de encaixe no topo de cada coluna (conector de empilhamento). */
export const STACK_PEG = {
  radius: 0.055,
  height: 0.07,
  segments: 16,
} as const;

/** Parede traseira fechada (fundo do bloco). */
export const BACK_WALL = {
  thickness: 0.08,
} as const;

/** Lâmina colorida deslizante. */
export const BLADE = {
  thickness: 0.08, // espessura da lâmina
  recess: -0.14, // recuo da lâmina em relação à face externa
  widthRatio: 0.58, // proporção da largura útil ocupada pelo padrão gravado
  heightRatio: 0.62, // proporção da altura útil ocupada pelo padrão gravado
  engraveOffset: 0.004, // deslocamento do padrão para fora da superfície
} as const;

/** Trilhos-guia verticais que seguram as bordas da lâmina (canaleta de encaixe). */
export const BLADE_RAIL = {
  width: 0.05, // largura do trilho (no sentido da face)
  protrude: 0.05, // o quanto o trilho avança além da lâmina (em cada lado, no eixo de profundidade)
  heightInset: 0.02, // folga vertical do trilho em relação ao corpo
} as const;

/** Clipes/suportes no piso que prendem a base das lâminas. */
export const BASE_CLIP = {
  length: 0.46, // comprimento do clipe (acompanha a face)
  depth: 0.1, // profundidade do clipe
  height: 0.07, // altura acima do piso
  wallThickness: 0.04, // espessura das paredes do clipe em "U"
  edgeInset: 0.12, // recuo do clipe a partir da face interna
} as const;

// ─── Detalhes internos do bloco ──────────────────────────────────────────────

/**
 * Pilar de sustentação cilíndrico oco nos cantos internos. É o tubo vertical
 * com furo no topo que recebe o pino do bloco de cima (conector de empilhamento).
 */
export const PILLAR = {
  outerRadius: 0.09, // raio externo do tubo
  innerRadius: 0.045, // raio do furo (parte oca)
  segments: 24, // suavidade do cilindro
  inset: 0.0, // recuo do centro a partir do canto interno (0 = no canto)
  pin: true, // renderiza o pino conector no topo
  pinRadius: 0.055, // raio externo do pino superior
  pinInnerRadius: 0.025, // furo do pino
  pinHeight: 0.16, // o quanto o pino se projeta acima do corpo
} as const;

/** Aleta triangular de reforço (gusset) que liga o pilar às paredes do canto. */
export const CORNER_GUSSET = {
  width: 0.32, // comprimento da parede diagonal
  height: 0.32, // altura da aleta
  thickness: 0.04, // espessura
} as const;

/** Bossas (furos de fixação) cilíndricas ocas no piso. */
export const FLOOR_BOSS = {
  outerRadius: 0.058,
  innerRadius: 0.024,
  height: 0.2,
  segments: 20,
  offsetX: 0.6, // posição lateral em fração da meia-largura
  offsetZ: -0.04, // leve deslocamento em profundidade
} as const;

/** Moldura retangular central elevada (suporte tipo "janela") no piso. */
export const CENTER_FRAME = {
  width: 0.5,
  depth: 0.26,
  height: 0.14,
  wallThickness: 0.04,
  offsetZ: -0.24, // deslocada para o fundo do piso
} as const;

/** Trilho fino no piso, com pequenos clipes em "C" nas pontas. */
export const FLOOR_RAIL = {
  length: 0.64,
  width: 0.05,
  height: 0.1,
  offsetZ: 0.28, // deslocado para a frente do piso
  clipLength: 0.11,
  clipWidth: 0.1,
  clipThickness: 0.04,
} as const;

/** Marcas circulares de ejeção no piso (detalhe sutil e plano). */
export const FLOOR_PAD = {
  radius: 0.075,
  height: 0.008,
  segments: 20,
  /** Posições [x, z] em fração da meia-dimensão do piso. */
  positions: [
    [-0.46, 0.46],
    [0.46, 0.46],
    [-0.46, -0.12],
    [0.46, -0.12],
  ] as ReadonlyArray<readonly [number, number]>,
} as const;

/** Tampa (lid) que fecha o topo do bloco superior. */
export const LID = {
  height: 0.22,
  lipHeight: 0.06, // saliência inferior que encaixa entre as colunas
  lipInset: 0.13, // recuo da saliência em relação à borda externa
} as const;

// ─── Materiais ───────────────────────────────────────────────────────────────

/** Parâmetros do material plástico (meshStandardMaterial). */
export const PLASTIC_MATERIAL = {
  roughness: 0.55,
  metalness: 0.0,
} as const;

/** Cor da gravação dos padrões nas lâminas. */
export const ENGRAVE_COLOR = '#050505';
export const ENGRAVE_LINE_WIDTH = 2.7;

// ─── Cores ───────────────────────────────────────────────────────────────────
// Alinhadas com --color-bloco-*, --color-lamina-* e a tampa do global.css.

export const COR_BLOCO_HEX: Record<CorBloco, string> = {
  [CorBloco.Preto]: '#252527',
  [CorBloco.Vermelho]: '#CC2222',
  [CorBloco.Azul]: '#1A55CC',
};

export const COR_LAMINA_HEX: Record<CorLamina, string> = {
  [CorLamina.Vermelho]: '#E6463F',
  [CorLamina.Azul]: '#1A55CC',
  [CorLamina.Amarelo]: '#E6B800',
  [CorLamina.Verde]: '#229944',
  [CorLamina.Preto]: '#484848',
  [CorLamina.Branco]: '#F0F0EE',
};

export const COR_TAMPA_HEX: Record<CorTampa, string> = {
  [CorTampa.Preto]: '#252527',
  [CorTampa.Vermelho]: '#CC2222',
  [CorTampa.Azul]: '#1A55CC',
};

/** Cor de fallback caso a chave não exista no mapa. */
export const COR_BLOCO_FALLBACK = '#252527';
export const COR_LAMINA_FALLBACK = '#F0F0EE';
export const COR_TAMPA_FALLBACK = '#252527';
