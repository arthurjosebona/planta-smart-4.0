import { CENTER_FRAME } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface CenterFrameProps {
  /** Y do topo do piso (onde a moldura se apoia). */
  floorTopY: number;
  color: string;
}

/**
 * Atom: moldura retangular oca elevada no piso (suporte tipo "janela"). Composta
 * por quatro paredes finas formando um anel — reproduz o encaixe retangular
 * vazado visto no interior do bloco real.
 */
export function CenterFrame({ floorTopY, color }: CenterFrameProps) {
  const { width, depth, height, wallThickness, offsetZ } = CENTER_FRAME;
  const y = floorTopY + height / 2;
  const halfW = width / 2 - wallThickness / 2;
  const halfD = depth / 2 - wallThickness / 2;

  return (
    <group position={[0, y, offsetZ]}>
      {/* Paredes frente/fundo */}
      {[halfD, -halfD].map((z) => (
        <mesh key={`z-${z}`} position={[0, 0, z]}>
          <boxGeometry args={[width, height, wallThickness]} />
          <PlasticMat color={color} />
        </mesh>
      ))}
      {/* Paredes laterais */}
      {[halfW, -halfW].map((x) => (
        <mesh key={`x-${x}`} position={[x, 0, 0]}>
          <boxGeometry args={[wallThickness, height, depth - 2 * wallThickness]} />
          <PlasticMat color={color} />
        </mesh>
      ))}
    </group>
  );
}
