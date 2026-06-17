import { FLOOR_PAD } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface FloorPadProps {
  /** Posição [x, z] do centro do pad. */
  x: number;
  z: number;
  /** Y do topo do piso. */
  floorTopY: number;
  color: string;
}

/**
 * Atom: marca circular plana de ejeção no piso — detalhe sutil de moldagem
 * presente no fundo do bloco real.
 */
export function FloorPad({ x, z, floorTopY, color }: FloorPadProps) {
  const { radius, height, segments } = FLOOR_PAD;
  return (
    <mesh position={[x, floorTopY + height / 2, z]}>
      <cylinderGeometry args={[radius, radius, height, segments]} />
      <PlasticMat color={color} />
    </mesh>
  );
}
