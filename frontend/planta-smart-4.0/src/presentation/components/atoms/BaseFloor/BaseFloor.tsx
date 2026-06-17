import { RoundedBoxGeometry } from '@react-three/drei';
import { BLOCK, BASE_RIM, COLUMN } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface BaseFloorProps {
  /** Y da base (parte inferior) do piso. */
  baseBottomY: number;
  color: string;
  blockW?: number;
  blockD?: number;
  baseT?: number;
  colW?: number;
}

/**
 * Atom: piso do bloco — a placa de base arredondada mais a borda elevada
 * (moldura) que corre pelo perímetro entre as colunas, dando o aspecto de
 * bandeja do produto real.
 */
export function BaseFloor({
  baseBottomY,
  color,
  blockW = BLOCK.width,
  blockD = BLOCK.depth,
  baseT = BLOCK.baseThickness,
  colW = COLUMN.width,
}: BaseFloorProps) {
  const floorTopY = baseBottomY + baseT;
  const rimY = floorTopY + BASE_RIM.height / 2;
  const t = BASE_RIM.thickness;
  const spanX = blockW - 2 * colW;
  const spanZ = blockD - 2 * colW;

  return (
    <group>
      {/* Placa de base */}
      <mesh position={[0, baseBottomY + baseT / 2, 0]}>
        <RoundedBoxGeometry args={[blockW, baseT, blockD]} radius={BLOCK.cornerRadius} />
        <PlasticMat color={color} />
      </mesh>

      {/* Borda elevada (moldura) — frente e fundo */}
      {[blockD / 2 - t / 2, -(blockD / 2 - t / 2)].map((z) => (
        <mesh key={`fz-${z}`} position={[0, rimY, z]}>
          <boxGeometry args={[spanX, BASE_RIM.height, t]} />
          <PlasticMat color={color} />
        </mesh>
      ))}

      {/* Borda elevada (moldura) — laterais */}
      {[blockW / 2 - t / 2, -(blockW / 2 - t / 2)].map((x) => (
        <mesh key={`lx-${x}`} position={[x, rimY, 0]}>
          <boxGeometry args={[t, BASE_RIM.height, spanZ]} />
          <PlasticMat color={color} />
        </mesh>
      ))}
    </group>
  );
}
