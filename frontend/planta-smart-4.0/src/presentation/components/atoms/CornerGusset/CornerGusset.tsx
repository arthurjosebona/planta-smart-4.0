import { CORNER_GUSSET } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface CornerGussetProps {
  /** Posição [x, z] do canto interno (centro da aleta). */
  x: number;
  z: number;
  /** Sinais do canto (sx, sz ∈ {-1, 1}) — definem a orientação diagonal. */
  sx: number;
  sz: number;
  /** Y do pé da aleta (topo do piso). */
  baseY: number;
  color: string;
  height?: number;
}

/**
 * Atom: aleta triangular/diagonal de reforço no canto. É uma parede fina girada
 * 45° que atravessa o canto, ligando o pilar de sustentação às paredes do bloco
 * — o reforço visto no interior da peça real.
 */
export function CornerGusset({
  x,
  z,
  sx,
  sz,
  baseY,
  color,
  height = CORNER_GUSSET.height,
}: CornerGussetProps) {
  // A diagonal do canto alinha-se a 45°; o sinal depende do quadrante.
  const rotY = sx * sz > 0 ? Math.PI / 4 : -Math.PI / 4;

  return (
    <mesh position={[x, baseY + height / 2, z]} rotation={[0, rotY, 0]}>
      <boxGeometry args={[CORNER_GUSSET.width, height, CORNER_GUSSET.thickness]} />
      <PlasticMat color={color} />
    </mesh>
  );
}
