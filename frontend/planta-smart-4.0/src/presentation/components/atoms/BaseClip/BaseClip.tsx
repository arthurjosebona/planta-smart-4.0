import { PosicaoLamina } from '@enums/PosicaoLamina';
import { BLADE, BASE_CLIP } from '@config/blockModel';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';

interface BaseClipProps {
  face: PosicaoLamina;
  /** Y do topo do piso (onde o clipe se apoia). */
  floorTopY: number;
  color: string;
  blockW: number;
  blockD: number;
  colW: number;
  bladeT?: number;
  bladeRecess?: number;
}

/**
 * Atom: clipe/suporte no piso que prende a base de uma lâmina. São duas paredes
 * baixas paralelas à face, formando uma canaleta exatamente sob a linha da
 * lâmina ([[Blade]]) — a lâmina encaixa nesse sulco, como no bloco real.
 */
export function BaseClip({
  face,
  floorTopY,
  color,
  blockW,
  blockD,
  colW,
  bladeT = BLADE.thickness,
  bladeRecess = BLADE.recess,
}: BaseClipProps) {
  const { length, height, wallThickness } = BASE_CLIP;
  const y = floorTopY + height / 2;
  // Distância do centro da lâmina a cada parede da canaleta.
  const half = bladeT / 2 + wallThickness / 2;

  if (face === PosicaoLamina.Frente) {
    const bladeZ = blockD / 2 - colW - bladeRecess - bladeT / 2;
    return (
      <group>
        {[bladeZ - half, bladeZ + half].map((z) => (
          <mesh key={z} position={[0, y, z]}>
            <boxGeometry args={[length, height, wallThickness]} />
            <PlasticMat color={color} />
          </mesh>
        ))}
      </group>
    );
  }

  const sign = face === PosicaoLamina.Esquerda ? -1 : 1;
  const bladeX = sign * (blockW / 2 - colW - bladeRecess - bladeT / 2);
  return (
    <group>
      {[bladeX - half, bladeX + half].map((x) => (
        <mesh key={x} position={[x, y, 0]}>
          <boxGeometry args={[wallThickness, height, length]} />
          <PlasticMat color={color} />
        </mesh>
      ))}
    </group>
  );
}
