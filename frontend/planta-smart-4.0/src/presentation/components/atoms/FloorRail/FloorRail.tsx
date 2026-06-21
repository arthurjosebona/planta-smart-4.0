import { FLOOR_RAIL } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface FloorRailProps {
  /** Y do topo do piso (onde o trilho se apoia). */
  floorTopY: number;
  color: string;
}

/**
 * Atom: trilho fino no piso com pequenos clipes em "C" nas pontas — o guia
 * baixo e comprido visto no interior do bloco real.
 */
export function FloorRail({ floorTopY, color }: FloorRailProps) {
  const { length, width, height, offsetZ, clipLength, clipWidth, clipThickness } = FLOOR_RAIL;
  const y = floorTopY + height / 2;
  const endX = length / 2;

  return (
    <group position={[0, y, offsetZ]}>
      {/* Barra principal */}
      <mesh>
        <boxGeometry args={[length, height, width]} />
        <PlasticMat color={color} />
      </mesh>

      {/* Clipes em "C" nas duas pontas (parede de topo + base) */}
      {[endX, -endX].map((x) => (
        <group key={x} position={[x, 0, 0]}>
          {[clipWidth / 2 - clipThickness / 2, -(clipWidth / 2 - clipThickness / 2)].map((z) => (
            <mesh key={z} position={[0, 0, z]}>
              <boxGeometry args={[clipLength, height, clipThickness]} />
              <PlasticMat color={color} />
            </mesh>
          ))}
        </group>
      ))}
    </group>
  );
}
