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
 * Atom: tampa que fecha o topo do bloco superior. Transborda levemente além do
 * bloco (overhang) para cobrir o topo das colunas nos cantos — a região de encaixe.
 */
export function Tampa({
  color,
  y,
  blockW = BLOCK.width,
  blockD = BLOCK.depth,
  lidH = LID.height,
}: TampaProps) {
  const capW = blockW + 2 * LID.overhang;
  const capD = blockD + 2 * LID.overhang;

  return (
    <mesh position={[0, y + lidH / 2, 0]}>
      <RoundedBoxGeometry args={[capW, lidH, capD]} />
      <PlasticMat color={color} />
    </mesh>
  );
}
