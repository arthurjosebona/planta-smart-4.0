import { useMemo } from 'react';
import { Line } from '@react-three/drei';
import * as THREE from 'three';
import { PadraoLamina } from '@enums/PadraoLamina';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

// ─── Tipos ────────────────────────────────────────────────────────────────────

type Point2D = [number, number];

// ─── Helpers de padrão ────────────────────────────────────────────────────────

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
      [0, 0.56], [0.12, 0.18], [0.54, 0.18], [0.2, -0.06], [0.32, -0.47],
      [0, -0.22], [-0.32, -0.47], [-0.2, -0.06], [-0.54, 0.18], [-0.12, 0.18], [0, 0.56],
    ],
  ],
  [PadraoLamina.Casa]: [
    [[-0.5, -0.44], [0.5, -0.44], [0.5, 0.22], [0.7, 0.02], [0.0, 0.58], [-0.7, 0.02], [-0.5, 0.22], [-0.5, -0.44]],
    [[0.38, 0.28], [0.38, 0.5], [0.54, 0.5], [0.54, 0.14]],
    [[-0.18, -0.44], [-0.18, -0.08], [0.18, -0.08], [0.18, -0.44]],
    [[-0.22, 0.03], [0.22, 0.03], [0.22, 0.23], [-0.22, 0.23], [-0.22, 0.03]],
    [[-0.22, 0.13], [0.22, 0.13]],
  ],
  [PadraoLamina.Navio]: [
    [[-0.72, -0.34], [0.55, -0.34], [0.78, 0.02], [-0.72, 0.02], [-0.72, -0.34]],
    [[-0.55, 0.02], [-0.55, 0.34], [0.5, 0.34], [0.5, 0.02]],
    [[-0.35, 0.34], [-0.35, 0.62], [-0.12, 0.62], [-0.12, 0.34]],
    [[0.02, 0.34], [0.02, 0.62], [0.25, 0.62], [0.25, 0.34]],
    circlePath(-0.32, 0.17, 0.07),
    circlePath(-0.02, 0.17, 0.07),
    circlePath(0.28, 0.17, 0.07),
  ],
};

function pathBounds(paths: Point2D[][]): { minU: number; maxU: number; minV: number; maxV: number } {
  let minU = Infinity, maxU = -Infinity, minV = Infinity, maxV = -Infinity;
  for (const path of paths)
    for (const [u, v] of path) {
      if (u < minU) minU = u; if (u > maxU) maxU = u;
      if (v < minV) minV = v; if (v > maxV) maxV = v;
    }
  return { minU, maxU, minV, maxV };
}

function patternTo3D(
  padrao: PadraoLamina,
  face: PosicaoLamina,
  bladeX: number,
  bladeZ: number,
  bladeY: number,
  bladeWidth: number,
  bladeHeight: number,
  bladeT: number,
): THREE.Vector3[][] {
  const paths = PATTERN_PATHS[padrao];
  const { minU, maxU, minV, maxV } = pathBounds(paths);
  const scale = Math.min((bladeWidth * 0.58) / (maxU - minU), (bladeHeight * 0.62) / (maxV - minV));
  const cx = face === PosicaoLamina.Frente ? 0 : bladeX;
  const cy = bladeY;

  return paths.map((path) =>
    path.map(([u, v]) => {
      if (face === PosicaoLamina.Frente)
        return new THREE.Vector3(cx + u * scale, cy + v * scale, bladeZ + bladeT / 2 + 0.004);
      if (face === PosicaoLamina.Esquerda)
        return new THREE.Vector3(bladeX - bladeT / 2 - 0.004, cy + v * scale, bladeZ + u * scale);
      return new THREE.Vector3(bladeX + bladeT / 2 + 0.004, cy + v * scale, bladeZ - u * scale);
    })
  );
}

// ─── Blade ────────────────────────────────────────────────────────────────────
// Molecule: lâmina colorida com padrão gravado opcional.

interface BladeProps {
  face: PosicaoLamina;
  cor: string;
  padrao: PadraoLamina | null;
  blockY: number;
  // dimensões recebidas do Block pai
  blockW?: number;
  blockD?: number;
  blockH?: number;
  baseT?: number;
  colW?: number;
  bladeT?: number;
  bladeRecess?: number;
}

export function Blade({
  face,
  cor,
  padrao,
  blockY,
  blockW = 1.7,
  blockD = 1.7,
  blockH = 0.71,
  baseT = 0.1,
  colW = 0.22,
  bladeT = 0.08,
  bladeRecess = -0.14,
}: BladeProps) {
  const bodyH = blockH - baseT;

  let bW: number, bH: number, bD: number;
  let px: number, py: number, pz: number;

  if (face === PosicaoLamina.Frente) {
    bW = blockW - 2 * colW;
    bH = bodyH;
    bD = bladeT;
    px = 0;
    py = blockY + baseT + bodyH / 2;
    pz = blockD / 2 - colW - bladeRecess - bladeT / 2;
  } else {
    const span = blockD - 2 * colW;
    bW = bladeT;
    bH = bodyH;
    bD = span;
    px = face === PosicaoLamina.Esquerda
      ? -blockW / 2 + colW + bladeRecess + bladeT / 2
      : blockW / 2 - colW - bladeRecess - bladeT / 2;
    py = blockY + baseT + bodyH / 2;
    pz = 0;
  }

  const patternLines = useMemo(() => {
    if (!padrao) return null;
    const bladeWidth = face === PosicaoLamina.Frente ? bW : bD;
    return patternTo3D(padrao, face, px, pz, py, bladeWidth, bH, bladeT);
  }, [padrao, face, px, py, pz, bW, bH, bD, bladeT]);

  return (
    <group>
      <mesh position={[px, py, pz]}>
        <boxGeometry args={[bW, bH, bD]} />
        <PlasticMat color={cor} />
      </mesh>
      {patternLines?.map((pts, i) => (
        <Line key={i} points={pts} color="#050505" lineWidth={2.7} />
      ))}
    </group>
  );
}