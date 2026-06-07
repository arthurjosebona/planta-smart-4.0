import { RoundedBoxGeometry } from '@react-three/drei';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface TampaProps {
  color: string;
  y: number;
  blockW?: number;
  blockD?: number;
  lidH?: number;
}

export function Tampa({ color, y, blockW = 1.7, blockD = 1.7, lidH = 0.22 }: TampaProps) {
  return (
    <mesh position={[0, y + lidH / 2, 0]}>
      <RoundedBoxGeometry args={[blockW, lidH, blockD]} />
      <PlasticMat color={color} />
    </mesh>
  );
}
