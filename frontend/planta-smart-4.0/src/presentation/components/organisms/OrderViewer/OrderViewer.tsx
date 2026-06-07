import { useMemo } from 'react';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, RoundedBoxGeometry, Line } from '@react-three/drei';
import * as THREE from 'three';
import { PadraoLamina } from '@enums/PadraoLamina';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { ConfigBloco } from '@valueObjects/ConfigBloco';
import { StoreModel } from '@pages/Store/StoreModel';

// ─── Dimension constants ──────────────────────────────────────────────────────

const BLOCK_W: number = 1.7;
const BLOCK_D: number = 1.7;
const BLOCK_H: number = 0.71;
const BASE_T: number = 0.1;
const WALL_T: number = 0.28;
const COL_W: number = 0.22;
const COL_RADIUS: number = 0.045;
const BLADE_T: number = 0.08;
const BLADE_RECESS: number = -0.14;
const LID_H: number = 0.22;
const LID_RADIUS: number = 0.1;

// ─── Color maps ───────────────────────────────────────────────────────────────

const COR_BLOCO_HEX: Record<string, string> = {
  preto: '#484848',
  vermelho: '#e6463f',
  azul: '#0065d7',
};

const COR_LAMINA_HEX: Record<string, string> = {
  vermelho: '#e6463f',
  azul: '#1a55cc',
  amarelo: '#e6b800',
  verde: '#229944',
  preto: '#484848',
  branco: '#f0f0ee',
};

const COR_TAMPA_HEX: Record<string, string> = {
  preto: '#484848',
  vermelho: '#e6463f',
  azul: '#1a55cc',
};

// ─── Pattern paths ────────────────────────────────────────────────────────────

type Point2D = [number, number];

/** Generate a circle path as an array of 2D points */
function circlePath(cx: number, cy: number, r: number, steps = 20): Point2D[] {
  const pts: Point2D[] = [];
  for (let i = 0; i <= steps; i++) {
    const a = (i / steps) * Math.PI * 2;
    pts.push([cx + Math.cos(a) * r, cy + Math.sin(a) * r]);
  }
  return pts;
}

const PATTERN_PATHS: Record<PadraoLamina, Point2D[][]> = {
  [PadraoLamina.Nenhum]: [],
  [PadraoLamina.Estrela]: [
    [
      [0, 0.56],
      [0.12, 0.18],
      [0.54, 0.18],
      [0.2, -0.06],
      [0.32, -0.47],
      [0, -0.22],
      [-0.32, -0.47],
      [-0.2, -0.06],
      [-0.54, 0.18],
      [-0.12, 0.18],
      [0, 0.56],
    ],
  ],
  [PadraoLamina.Casa]: [
    [
      [-0.5, -0.44],
      [0.5, -0.44],
      [0.5, 0.22],
      [0.7, 0.02],
      [0.0, 0.58],
      [-0.7, 0.02],
      [-0.5, 0.22],
      [-0.5, -0.44],
    ],
    [
      [0.38, 0.28],
      [0.38, 0.5],
      [0.54, 0.5],
      [0.54, 0.14],
    ],
    [
      [-0.18, -0.44],
      [-0.18, -0.08],
      [0.18, -0.08],
      [0.18, -0.44],
    ],
    [
      [-0.22, 0.03],
      [0.22, 0.03],
      [0.22, 0.23],
      [-0.22, 0.23],
      [-0.22, 0.03],
    ],
    [
      [-0.22, 0.13],
      [0.22, 0.13],
    ],
  ],
  [PadraoLamina.Navio]: [
    [
      [-0.72, -0.34],
      [0.55, -0.34],
      [0.78, 0.02],
      [-0.72, 0.02],
      [-0.72, -0.34],
    ],
    [
      [-0.55, 0.02],
      [-0.55, 0.34],
      [0.5, 0.34],
      [0.5, 0.02],
    ],
    [
      [-0.35, 0.34],
      [-0.35, 0.62],
      [-0.12, 0.62],
      [-0.12, 0.34],
    ],
    [
      [0.02, 0.34],
      [0.02, 0.62],
      [0.25, 0.62],
      [0.25, 0.34],
    ],
    circlePath(-0.32, 0.17, 0.07),
    circlePath(-0.02, 0.17, 0.07),
    circlePath(0.28, 0.17, 0.07),
  ],
};

/** Compute bounding box of all 2D paths */
function pathBounds(paths: Point2D[][]): {
  minU: number;
  maxU: number;
  minV: number;
  maxV: number;
} {
  let minU = Infinity,
    maxU = -Infinity,
    minV = Infinity,
    maxV = -Infinity;
  for (const path of paths) {
    for (const [u, v] of path) {
      if (u < minU) minU = u;
      if (u > maxU) maxU = u;
      if (v < minV) minV = v;
      if (v > maxV) maxV = v;
    }
  }
  return { minU, maxU, minV, maxV };
}

// ─── Convert 2D pattern paths → 3D points for a given face ───────────────────

function patternTo3D(
  padrao: PadraoLamina,
  face: PosicaoLamina,
  bladeX: number,
  bladeZ: number,
  bladeY: number, // center Y of blade
  bladeWidth: number, // span in U direction
  bladeHeight: number // span in V direction
): THREE.Vector3[][] {
  const paths = PATTERN_PATHS[padrao];
  const { minU, maxU, minV, maxV } = pathBounds(paths);
  const boundsW = maxU - minU;
  const boundsH = maxV - minV;

  const safeW = bladeWidth * 0.58;
  const safeH = bladeHeight * 0.62;
  const scale = Math.min(safeW / boundsW, safeH / boundsH);

  // center of the pattern in object-space (normalized coords are centered ~0)
  const cx = face === PosicaoLamina.Frente ? 0 : bladeX;
  const cy = bladeY;
  const cz = face === PosicaoLamina.Frente ? bladeZ : bladeZ;

  return paths.map((path) =>
    path.map(([u, v]) => {
      if (face === PosicaoLamina.Frente) {
        return new THREE.Vector3(cx + u * scale, cy + v * scale, bladeZ + BLADE_T / 2 + 0.004);
      } else if (face === PosicaoLamina.Esquerda) {
        return new THREE.Vector3(bladeX - BLADE_T / 2 - 0.004, cy + v * scale, cz + u * scale);
      } else {
        // direita
        return new THREE.Vector3(bladeX + BLADE_T / 2 + 0.004, cy + v * scale, cz - u * scale);
      }
    })
  );
}

// ─── Plastic material ─────────────────────────────────────────────────────────

function PlasticMat({ color }: { color: string }) {
  return <meshStandardMaterial color={color} roughness={0.55} metalness={0.0} />;
}

// ─── Single blade ─────────────────────────────────────────────────────────────

interface BladeProps {
  face: PosicaoLamina;
  cor: string;
  padrao: PadraoLamina | null;
  blockY: number; // Y base of the block (bottom of base)
}

function Blade({ face, cor, padrao, blockY }: BladeProps) {
  const bodyH = BLOCK_H - BASE_T;

  // ── dimensions & position ──────────────────────────────────────────────────

  let bW: number, bH: number, bD: number;
  let px: number, py: number, pz: number;

  if (face === PosicaoLamina.Frente) {
    bW = BLOCK_W - 2 * COL_W;
    bH = bodyH;
    bD = BLADE_T;
    px = 0;
    py = blockY + BASE_T + bodyH / 2;
    pz = BLOCK_D / 2 - COL_W - BLADE_RECESS - BLADE_T / 2;
  } else {
    // side blades span from back wall inner face to front column inner face
    const span = BLOCK_D - 2 * COL_W;
    bW = BLADE_T;
    bH = bodyH;
    bD = span;

    const centerZ = 0;

    px =
      face === PosicaoLamina.Esquerda
        ? -BLOCK_W / 2 + COL_W + BLADE_RECESS + BLADE_T / 2
        : BLOCK_W / 2 - COL_W - BLADE_RECESS - BLADE_T / 2;
    py = blockY + BASE_T + bodyH / 2;
    pz = centerZ;
  }

  // ── pattern lines ──────────────────────────────────────────────────────────

  const patternLines = useMemo(() => {
    if (!padrao) return null;

    // For patterns: determine the "blade width" (U axis) and center point in world space
    let bladeWidth: number, bladeX: number, bladeZ: number;

    if (face === PosicaoLamina.Frente) {
      bladeWidth = bW;
      bladeX = 0; // not used for frente
      bladeZ = pz;
    } else {
      bladeWidth = bD;
      bladeX = px;
      bladeZ = pz;
    }

    return patternTo3D(padrao, face, bladeX, bladeZ, py, bladeWidth, bH);
  }, [padrao, face, px, py, pz, bW, bH, bD]);

  return (
    <group>
      <mesh position={[px, py, pz]}>
        <boxGeometry args={[bW, bH, bD]} />
        <PlasticMat color={cor} />
      </mesh>

      {patternLines &&
        patternLines.map((pts, i) => <Line key={i} points={pts} color="#050505" lineWidth={2.7} />)}
    </group>
  );
}

// ─── Single block assembly ────────────────────────────────────────────────────

interface BlockProps {
  config: ConfigBloco;
  blockY: number;
}

function Block({ config, blockY }: BlockProps) {
  const hex = COR_BLOCO_HEX[config.cor];
  const bodyH = BLOCK_H - BASE_T;

  return (
    <group>
      {/* Peça 1 — Base */}
      <mesh position={[0, blockY + BASE_T / 2, 0]}>
        <RoundedBoxGeometry args={[BLOCK_W, BASE_T, BLOCK_D]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Peça 2a — Coluna traseira esquerda */}
      <mesh
        position={[-BLOCK_W / 2 + COL_W / 2, blockY + BASE_T + bodyH / 2, -BLOCK_D / 2 + COL_W / 2]}
      >
        <RoundedBoxGeometry args={[COL_W, bodyH + COL_RADIUS * 4, COL_W]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Peça 2b — Coluna traseira direita */}
      <mesh
        position={[BLOCK_W / 2 - COL_W / 2, blockY + BASE_T + bodyH / 2, -BLOCK_D / 2 + COL_W / 2]}
      >
        <RoundedBoxGeometry args={[COL_W, bodyH + COL_RADIUS * 4, COL_W]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Peça 2c — Lâmina traseira */}
      <mesh position={[0, blockY + BASE_T + bodyH / 2, -BLOCK_D / 2 + BLADE_T / 1.08]}>
        <boxGeometry args={[BLOCK_W - 2 * COL_W, bodyH, BLADE_T]} />
        <PlasticMat color={hex} />
      </mesh>
      {/* Peça 3 — Coluna frontal esquerda */}
      <mesh
        position={[-BLOCK_W / 2 + COL_W / 2, blockY + BASE_T + bodyH / 2, BLOCK_D / 2 - COL_W / 2]}
      >
        <RoundedBoxGeometry args={[COL_W, bodyH + COL_RADIUS * 4, COL_W]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Peça 4 — Coluna frontal direita */}
      <mesh
        position={[BLOCK_W / 2 - COL_W / 2, blockY + BASE_T + bodyH / 2, BLOCK_D / 2 - COL_W / 2]}
      >
        <RoundedBoxGeometry args={[COL_W, bodyH + COL_RADIUS * 4, COL_W]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Lâminas */}
      {(['frente', 'esquerda', 'direita'] as PosicaoLamina[]).map((face) => {
        const lamina = config.laminas[face];
        if (!lamina.cor) return null;
        return (
          <Blade
            key={face}
            face={face}
            cor={COR_LAMINA_HEX[lamina.cor]}
            padrao={lamina.padrao}
            blockY={blockY}
          />
        );
      })}
    </group>
  );
}

// ─── Tampa (lid) ──────────────────────────────────────────────────────────────

function Tampa({ color, y }: { color: string; y: number }) {
  return (
    <mesh position={[0, y + LID_H / 2, 0]}>
      <RoundedBoxGeometry args={[BLOCK_W, LID_H, BLOCK_D]} />
      <PlasticMat color={color} />
    </mesh>
  );
}

// ─── Scene — stacks N blocks + lid ───────────────────────────────────────────

interface BlockSceneProps {
  state: StoreModel;
}

function BlockScene({ state }: BlockSceneProps) {
  const { numBlocos, corTampa, blocos } = state;

  // Compute Y offset for each block so they stack
  const blockOffsets = useMemo(() => {
    const offsets: number[] = [];
    let y = 0;
    for (let i = 0; i < numBlocos; i++) {
      offsets.push(y);
      y += BLOCK_H;
    }
    return offsets;
  }, [numBlocos]);

  const totalH = BLOCK_H * numBlocos;

  // Center the entire stack vertically around y=0
  const centerY = -totalH / 2;

  return (
    <group position={[0, centerY, 0]}>
      {Array.from({ length: numBlocos }, (_, i) => (
        <Block key={i} config={blocos[i]} blockY={blockOffsets[i]} />
      ))}
      <Tampa color={COR_TAMPA_HEX[corTampa]} y={totalH} />
    </group>
  );
}

// ─── Public component ─────────────────────────────────────────────────────────

interface BlockViewerProps {
  state: StoreModel;
}

export function OrderViewer({ state }: BlockViewerProps) {
  return (
    <Canvas camera={{ position: [0, 1.0, 8], fov: 38 }} style={{ background: '#f2f1ef', flex: 1 }}>
      <ambientLight intensity={0.75} />
      <directionalLight position={[4, 6, 5]} intensity={0.7} />
      <directionalLight position={[-4, 2, -4]} intensity={0.3} />
      <BlockScene state={state} />
      <OrbitControls enablePan={false} minDistance={3} maxDistance={16} />
    </Canvas>
  );
}
