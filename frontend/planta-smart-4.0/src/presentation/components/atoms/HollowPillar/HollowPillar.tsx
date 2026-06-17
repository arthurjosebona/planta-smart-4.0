import { useMemo } from 'react';
import * as THREE from 'three';
import { PILLAR } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

/**
 * Cria a geometria de um tubo (cilindro oco) por revolução do perfil da parede
 * em torno do eixo Y (LatheGeometry). Construção robusta — gera paredes interna
 * e externa, além das coroas de topo e base, sem os problemas de triangulação
 * (valores NaN) que o ExtrudeGeometry de um anel pode produzir. O tubo já nasce
 * em pé, com altura de 0 a `depth`.
 */
function makeTube(outer: number, inner: number, depth: number, segments: number) {
  const profile = [
    new THREE.Vector2(inner, 0),
    new THREE.Vector2(outer, 0),
    new THREE.Vector2(outer, depth),
    new THREE.Vector2(inner, depth),
    new THREE.Vector2(inner, 0),
  ];
  return new THREE.LatheGeometry(profile, segments);
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
      {/* O tubo (LatheGeometry) já nasce em pé no eixo Y — sem rotação. */}
      <mesh geometry={bodyGeom} position={[x, baseY, z]}>
        <PlasticMat color={color} />
      </mesh>
      {pinGeom && (
        <mesh geometry={pinGeom} position={[x, baseY + bodyHeight, z]}>
          <PlasticMat color={color} />
        </mesh>
      )}
    </group>
  );
}
