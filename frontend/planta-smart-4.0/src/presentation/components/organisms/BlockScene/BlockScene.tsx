import { useMemo } from 'react';
import { StoreModel } from '@pages/Store/StoreModel';
import {
  BLOCK,
  COLUMN,
  BLADE,
  LID,
  COR_TAMPA_HEX,
  COR_TAMPA_FALLBACK,
  COR_BLOCO_BLUEPRINT,
} from '@config/blockModel';
import { Block } from '../Block/Block';
import { Tampa } from '@components/atoms/Tampa/Tampa';

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
  blockW = BLOCK.width,
  blockD = BLOCK.depth,
  blockH = BLOCK.height,
  baseT = BLOCK.baseThickness,
  colW = COLUMN.width,
  colRadius = COLUMN.radius,
  bladeT = BLADE.thickness,
  bladeRecess = BLADE.recess,
  lidH = LID.height,
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

  // Tampa sem cor escolhida → renderiza como blueprint (cinza claro sólido).
  const tampaColor =
    corTampa === null ? COR_BLOCO_BLUEPRINT : (COR_TAMPA_HEX[corTampa] ?? COR_TAMPA_FALLBACK);

  return (
    <group position={[0, centerY, 0]}>
      {Array.from({ length: numBlocos }, (_, i) => (
        <Block key={i} config={blocos[i]} blockY={blockOffsets[i]} {...blockDimProps} />
      ))}
      <Tampa color={tampaColor} y={totalH} blockW={blockW} blockD={blockD} lidH={lidH} />
    </group>
  );
}