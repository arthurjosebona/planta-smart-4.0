import { useMemo } from 'react';
import { StoreModel } from '@pages/Store/StoreModel';
import { Block } from '../Block/Block';
import { Tampa } from '@components/atoms/Tampa/Tampa';

// Alinhado com --color-nav-red, --color-bloco-azul, --color-bloco-preto
const COR_TAMPA_HEX: Record<string, string> = {
  preto:    '#252527',
  vermelho: '#CC2222',
  azul:     '#1A55CC',
};

interface BlockSceneProps {
  state: StoreModel;
  blockW?: number;
  blockD?: number;
  blockH?: number;
  baseT?: number;
  colW?: number;
  colRadius?: number;
  bladeT?: number;
  bladeRecess?: number;
  lidH?: number;
}

export function BlockScene({
  state,
  blockW = 1.7,
  blockD = 1.7,
  blockH = 0.71,
  baseT = 0.1,
  colW = 0.22,
  colRadius = 0.045,
  bladeT = 0.08,
  bladeRecess = -0.14,
  lidH = 0.22,
}: BlockSceneProps) {
  const { numBlocos, corTampa, blocos } = state;

  const blockOffsets = useMemo(() => {
    const offsets: number[] = [];
    let y = 0;
    for (let i = 0; i < numBlocos; i++) {
      offsets.push(y);
      y += blockH;
    }
    return offsets;
  }, [numBlocos, blockH]);

  const totalH  = blockH * numBlocos;
  const centerY = -totalH / 2;

  const blockDimProps = { blockW, blockD, blockH, baseT, colW, colRadius, bladeT, bladeRecess };

  return (
    <group position={[0, centerY, 0]}>
      {Array.from({ length: numBlocos }, (_, i) => (
        <Block key={i} config={blocos[i]} blockY={blockOffsets[i]} {...blockDimProps} />
      ))}
      <Tampa
        color={COR_TAMPA_HEX[corTampa] ?? '#252527'}
        y={totalH}
        blockW={blockW}
        blockD={blockD}
        lidH={lidH}
      />
    </group>
  );
}