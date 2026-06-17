import { RoundedBoxGeometry } from '@react-three/drei';
import { BLOCK, LID } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface TampaProps {
  color: string;
  /** Y da base da tampa (topo do bloco superior). */
  y: number;
  blockW?: number;
  blockD?: number;
  lidH?: number;
}

/**
 * Atom: tampa que fecha o topo do bloco superior. Além do corpo arredondado,
 * possui uma saliência inferior (lip) que desce entre as colunas, encaixando
 * na boca do bloco — como na tampa real.
 */
export function Tampa({
  color,
  y,
  blockW = BLOCK.width,
  blockD = BLOCK.depth,
  lidH = LID.height,
}: TampaProps) {
  return (
    <group>
      {/* Corpo da tampa */}
      <mesh position={[0, y + lidH / 2, 0]}>
        <RoundedBoxGeometry args={[blockW, lidH, blockD]} radius={BLOCK.cornerRadius} />
        <PlasticMat color={color} />
      </mesh>

      {/* Saliência de encaixe (lip) na face inferior */}
      <mesh position={[0, y - LID.lipHeight / 2, 0]}>
        <boxGeometry
          args={[blockW - 2 * LID.lipInset, LID.lipHeight, blockD - 2 * LID.lipInset]}
        />
        <PlasticMat color={color} />
      </mesh>
    </group>
  );
}
