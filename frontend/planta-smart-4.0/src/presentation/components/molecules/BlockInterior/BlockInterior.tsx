import { PILLAR, FLOOR_BOSS, FLOOR_PAD } from '@config/blockModel';
import { HollowPillar } from '@components/atoms/HollowPillar/HollowPillar';
import { CornerGusset } from '@components/atoms/CornerGusset/CornerGusset';
import { CenterFrame } from '@components/atoms/CenterFrame/CenterFrame';
import { FloorRail } from '@components/atoms/FloorRail/FloorRail';
import { FloorPad } from '@components/atoms/FloorPad/FloorPad';

interface BlockInteriorProps {
  /** Y da base (parte inferior) do bloco. */
  blockY: number;
  color: string;
  blockW: number;
  blockD: number;
  baseT: number;
  colW: number;
  bodyHeight: number;
}

/**
 * Molecule: reúne todos os detalhes internos que ficam sobre o piso do bloco —
 * pilares de sustentação ocos nos cantos ([[HollowPillar]]), aletas de reforço
 * ([[CornerGusset]]), bossas de fixação, a moldura central ([[CenterFrame]]),
 * o trilho do piso ([[FloorRail]]) e as marcas de ejeção ([[FloorPad]]).
 */
export function BlockInterior({
  blockY,
  color,
  blockW,
  blockD,
  baseT,
  colW,
  bodyHeight,
}: BlockInteriorProps) {
  const floorTopY = blockY + baseT;

  // Cantos internos (onde ficam pilares e aletas).
  const cornerX = blockW / 2 - colW - PILLAR.inset;
  const cornerZ = blockD / 2 - colW - PILLAR.inset;
  const corners: Array<{ sx: number; sz: number }> = [
    { sx: -1, sz: -1 },
    { sx: 1, sz: -1 },
    { sx: -1, sz: 1 },
    { sx: 1, sz: 1 },
  ];

  return (
    <group>
      {/* Pilares de sustentação + aletas de reforço nos quatro cantos */}
      {corners.map(({ sx, sz }) => {
        const x = sx * cornerX;
        const z = sz * cornerZ;
        return (
          <group key={`${sx},${sz}`}>
            <HollowPillar
              x={x}
              z={z}
              baseY={floorTopY}
              bodyHeight={bodyHeight}
              color={color}
              pin={PILLAR.pin}
            />
            <CornerGusset x={x} z={z} sx={sx} sz={sz} baseY={floorTopY} color={color} />
          </group>
        );
      })}

      {/* Bossas de fixação (cilindros ocos menores) nas laterais do piso */}
      {[-1, 1].map((sx) => (
        <HollowPillar
          key={`boss-${sx}`}
          x={sx * (blockW / 2) * FLOOR_BOSS.offsetX}
          z={blockD * FLOOR_BOSS.offsetZ}
          baseY={floorTopY}
          bodyHeight={FLOOR_BOSS.height}
          color={color}
          outerRadius={FLOOR_BOSS.outerRadius}
          innerRadius={FLOOR_BOSS.innerRadius}
          segments={FLOOR_BOSS.segments}
        />
      ))}

      {/* Moldura central + trilho do piso */}
      <CenterFrame floorTopY={floorTopY} color={color} />
      <FloorRail floorTopY={floorTopY} color={color} />

      {/* Marcas de ejeção */}
      {FLOOR_PAD.positions.map(([fx, fz], i) => (
        <FloorPad
          key={`pad-${i}`}
          x={fx * (blockW / 2)}
          z={fz * (blockD / 2)}
          floorTopY={floorTopY}
          color={color}
        />
      ))}
    </group>
  );
}
