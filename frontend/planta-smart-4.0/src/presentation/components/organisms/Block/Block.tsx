import { PosicaoLamina } from '@enums/PosicaoLamina';
import { ConfigBloco } from '@valueObjects/ConfigBloco';
import {
  BLOCK,
  COLUMN,
  BACK_WALL,
  BLADE,
  CENTER_HOLDER,
  COR_BLOCO_HEX,
  COR_LAMINA_HEX,
  COR_BLOCO_FALLBACK,
  COR_LAMINA_FALLBACK,
} from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';
import { BaseFloor } from '@components/atoms/BaseFloor/BaseFloor';
import { Column } from '@components/atoms/Column/Column';
import { BladeRail } from '@components/atoms/BladeRail/BladeRail';
import { BaseClip } from '@components/atoms/BaseClip/BaseClip';
import { Blade } from '@components/molecules/Blade/Blade';

/** As três faces abertas do bloco (a traseira é sempre fechada). */
const FACES_ABERTAS: PosicaoLamina[] = [
  PosicaoLamina.Frente,
  PosicaoLamina.Esquerda,
  PosicaoLamina.Direita,
];

interface BlockProps {
  config: ConfigBloco;
  blockY: number;
  blockW?: number;
  blockD?: number;
  blockH?: number;
  baseT?: number;
  colW?: number;
  colRadius?: number;
  bladeT?: number;
  bladeRecess?: number;
}

export function Block({
  config,
  blockY,
  blockW = BLOCK.width,
  blockD = BLOCK.depth,
  blockH = BLOCK.height,
  baseT = BLOCK.baseThickness,
  colW = COLUMN.width,
  bladeT = BLADE.thickness,
  bladeRecess = BLADE.recess,
}: BlockProps) {
  const hex = COR_BLOCO_HEX[config.cor] ?? COR_BLOCO_FALLBACK;
  const bodyH = blockH - baseT;
  const bodyCenterY = blockY + baseT + bodyH / 2;
  const floorTopY = blockY + baseT;

  // Posição dos centros das colunas nos quatro cantos.
  const cx = blockW / 2 - colW / 2;
  const cz = blockD / 2 - colW / 2;
  const corners: Array<[number, number]> = [
    [-cx, -cz],
    [cx, -cz],
    [-cx, cz],
    [cx, cz],
  ];

  const frameProps = { color: hex, blockW, blockD, colW };
  const bladeProps = { blockW, blockD, blockH, baseT, colW, bladeT, bladeRecess };

  return (
    <group>
      {/* Piso + moldura */}
      <BaseFloor baseBottomY={blockY} color={hex} blockW={blockW} blockD={blockD} baseT={baseT} colW={colW} />

      {/* Suporte central no piso */}
      <mesh position={[0, floorTopY + CENTER_HOLDER.height / 2, 0]}>
        <boxGeometry args={[CENTER_HOLDER.width, CENTER_HOLDER.height, CENTER_HOLDER.depth]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Colunas dos quatro cantos (com pino de encaixe no topo) */}
      {corners.map(([x, z]) => (
        <Column
          key={`${x},${z}`}
          x={x}
          z={z}
          bodyCenterY={bodyCenterY}
          bodyHeight={bodyH}
          color={hex}
          width={colW}
          withPeg
        />
      ))}

      {/* Parede traseira fechada */}
      <mesh position={[0, bodyCenterY, -blockD / 2 + colW - BACK_WALL.thickness / 2]}>
        <boxGeometry args={[blockW - 2 * colW, bodyH, BACK_WALL.thickness]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Estrutura de encaixe das lâminas (trilhos + clipes) nas faces abertas */}
      {FACES_ABERTAS.map((face) => (
        <group key={face}>
          <BladeRail
            face={face}
            bodyCenterY={bodyCenterY}
            bodyHeight={bodyH}
            bladeT={bladeT}
            bladeRecess={bladeRecess}
            {...frameProps}
          />
          <BaseClip
            face={face}
            floorTopY={floorTopY}
            bladeT={bladeT}
            bladeRecess={bladeRecess}
            {...frameProps}
          />
        </group>
      ))}

      {/* Lâminas coloridas */}
      {FACES_ABERTAS.map((face) => {
        const lamina = config.laminas[face];
        if (!lamina.cor) return null;
        return (
          <Blade
            key={`blade-${face}`}
            face={face}
            cor={COR_LAMINA_HEX[lamina.cor] ?? COR_LAMINA_FALLBACK}
            padrao={lamina.padrao}
            blockY={blockY}
            {...bladeProps}
          />
        );
      })}
    </group>
  );
}
