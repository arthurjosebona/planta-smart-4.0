import { useMemo } from 'react';
import * as THREE from 'three';
import { PILLAR } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

/** Cria a geometria de um tubo (cilindro oco) extrudando um anel. */
function makeTube(outer: number, inner: number, depth: number, segments: number) {
  const shape = new THREE.Shape();
  shape.absarc(0, 0, outer, 0, Math.PI * 2, false);
  const hole = new THREE.Path();
  hole.absarc(0, 0, inner, 0, Math.PI * 2, true);
  shape.holes.push(hole);
  return new THREE.ExtrudeGeometry(shape, {
    depth,
    bevelEnabled: false,
    curveSegments: segments,
  });
}

interface HollowPillarProps {
  /** Posição [x, z] do centro do pilar. */
  x: number;
  z: number;
  /** Y do pé do pilar (topo do piso). */
  baseY: number;
  /** Altura do corpo do pilar. */
  bodyHeight: number;
  color: string;
  outerRadius?: number;
  innerRadius?: number;
  segments?: number;
  /** Renderiza o pino conector (mais estreito) no topo. */
  pin?: boolean;
  pinRadius?: number;
  pinInnerRadius?: number;
  pinHeight?: number;
}

/**
 * Atom: pilar de sustentação cilíndrico oco. O corpo é um tubo extrudado (com
 * furo passante que aparenta um furo cego ao se apoiar no piso) e, opcionalmente,
 * um pino conector mais estreito no topo. Reutilizável também como bossa de
 * fixação no piso, bastando ajustar os raios.
 */
export function HollowPillar({
  x,
  z,
  baseY,
  bodyHeight,
  color,
  outerRadius = PILLAR.outerRadius,
  innerRadius = PILLAR.innerRadius,
  segments = PILLAR.segments,
  pin = false,
  pinRadius = PILLAR.pinRadius,
  pinInnerRadius = PILLAR.pinInnerRadius,
  pinHeight = PILLAR.pinHeight,
}: HollowPillarProps) {
  const bodyGeom = useMemo(
    () => makeTube(outerRadius, innerRadius, bodyHeight, segments),
    [outerRadius, innerRadius, bodyHeight, segments]
  );
  const pinGeom = useMemo(
    () => (pin ? makeTube(pinRadius, pinInnerRadius, pinHeight, segments) : null),
    [pin, pinRadius, pinInnerRadius, pinHeight, segments]
  );

  return (
    <group>
      {/* Extrude é gerado no plano XY; a rotação coloca o tubo na vertical (Y). */}
      <mesh geometry={bodyGeom} position={[x, baseY, z]} rotation={[-Math.PI / 2, 0, 0]}>
        <PlasticMat color={color} />
      </mesh>
      {pinGeom && (
        <mesh
          geometry={pinGeom}
          position={[x, baseY + bodyHeight, z]}
          rotation={[-Math.PI / 2, 0, 0]}
        >
          <PlasticMat color={color} />
        </mesh>
      )}
    </group>
  );
}
