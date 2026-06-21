import { RoundedBoxGeometry } from '@react-three/drei';
import { COLUMN } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';
import { StackPeg } from '@components/atoms/StackPeg/StackPeg';

interface ColumnProps {
  /** Posição [x, z] do centro da coluna. */
  x: number;
  z: number;
  /** Centro vertical (y) do corpo do bloco e altura desse corpo. */
  bodyCenterY: number;
  bodyHeight: number;
  color: string;
  width?: number;
  /** Renderiza o pino de encaixe no topo da coluna. */
  withPeg?: boolean;
}

/**
 * Atom: uma das quatro colunas estruturais do canto do bloco. A coluna se
 * estende um pouco além do corpo (overshoot) para criar a sobreposição visual
 * no empilhamento. Opcionalmente carrega um pino de encaixe ([[StackPeg]]) no topo.
 */
export function Column({
  x,
  z,
  bodyCenterY,
  bodyHeight,
  color,
  width = COLUMN.width,
  withPeg = false,
}: ColumnProps) {
  const fullHeight = bodyHeight + COLUMN.overshoot;
  const topY = bodyCenterY + fullHeight / 2;

  return (
    <group>
      <mesh position={[x, bodyCenterY, z]}>
        <RoundedBoxGeometry args={[width, fullHeight, width]} radius={COLUMN.radius} />
        <PlasticMat color={color} />
      </mesh>
      {withPeg && <StackPeg position={[x, topY, z]} color={color} />}
    </group>
  );
}
