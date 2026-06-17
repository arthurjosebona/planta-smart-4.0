import { STACK_PEG } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface StackPegProps {
  /** Posição do centro da base do pino [x, y, z]. */
  position: [number, number, number];
  color: string;
  radius?: number;
  height?: number;
}

/**
 * Atom: pino cilíndrico de encaixe no topo de uma coluna. É o conector que une
 * um bloco ao bloco (ou à tampa) acima dele.
 */
export function StackPeg({
  position,
  color,
  radius = STACK_PEG.radius,
  height = STACK_PEG.height,
}: StackPegProps) {
  const [x, y, z] = position;
  return (
    <mesh position={[x, y + height / 2, z]}>
      <cylinderGeometry args={[radius, radius, height, STACK_PEG.segments]} />
      <PlasticMat color={color} />
    </mesh>
  );
}
