import { PosicaoLamina } from '@enums/PosicaoLamina';
import { BLADE, BLADE_RAIL } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface BladeRailProps {
  face: PosicaoLamina;
  bodyCenterY: number;
  bodyHeight: number;
  color: string;
  blockW: number;
  blockD: number;
  colW: number;
  bladeT?: number;
  bladeRecess?: number;
}

/**
 * Atom: par de trilhos-guia verticais que formam a canaleta de encaixe de uma
 * face aberta. As lâminas ([[Blade]]) deslizam entre estes trilhos. Os trilhos
 * existem mesmo nas faces sem lâmina (são parte da estrutura do bloco), assim
 * como no produto real.
 */
export function BladeRail({
  face,
  bodyCenterY,
  bodyHeight,
  color,
  blockW,
  blockD,
  colW,
  bladeT = BLADE.thickness,
  bladeRecess = BLADE.recess,
}: BladeRailProps) {
  const railW = BLADE_RAIL.width;
  const railH = bodyHeight - BLADE_RAIL.heightInset * 2;
  const railSpan = bladeT + BLADE_RAIL.protrude * 2; // profundidade do trilho (eixo perpendicular à face)

  if (face === PosicaoLamina.Frente) {
    const edgeX = blockW / 2 - colW - railW / 2;
    const z = blockD / 2 - colW - bladeRecess - bladeT / 2;
    return (
      <group>
        {[-edgeX, edgeX].map((x) => (
          <mesh key={x} position={[x, bodyCenterY, z]}>
            <boxGeometry args={[railW, railH, railSpan]} />
            <PlasticMat color={color} />
          </mesh>
        ))}
      </group>
    );
  }

  // Faces laterais (esquerda / direita)
  const sign = face === PosicaoLamina.Esquerda ? -1 : 1;
  const x = sign * (blockW / 2 - colW - bladeRecess - bladeT / 2);
  const edgeZ = blockD / 2 - colW - railW / 2;
  return (
    <group>
      {[-edgeZ, edgeZ].map((z) => (
        <mesh key={z} position={[x, bodyCenterY, z]}>
          <boxGeometry args={[railSpan, railH, railW]} />
          <PlasticMat color={color} />
        </mesh>
      ))}
    </group>
  );
}
