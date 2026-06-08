import { RoundedBoxGeometry } from '@react-three/drei';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { ConfigBloco } from '@valueObjects/ConfigBloco';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';
import { Blade } from '@components/molecules/Blade/Blade';

// Alinhado com --color-bloco-* e --color-lamina-* do global.css
const COR_BLOCO_HEX: Record<string, string> = {
  preto:    '#252527',
  vermelho: '#CC2222',
  azul:     '#1A55CC',
};

const COR_LAMINA_HEX: Record<string, string> = {
  vermelho: '#E6463F',
  azul:     '#1A55CC',
  amarelo:  '#E6B800',
  verde:    '#229944',
  preto:    '#484848',
  branco:   '#F0F0EE',
};

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
  blockW = 1.7,
  blockD = 1.7,
  blockH = 0.71,
  baseT = 0.1,
  colW = 0.22,
  colRadius = 0.045,
  bladeT = 0.08,
  bladeRecess = -0.14,
}: BlockProps) {
  const hex    = COR_BLOCO_HEX[config.cor] ?? '#252527';
  const bodyH  = blockH - baseT;
  const bladeProps = { blockW, blockD, blockH, baseT, colW, bladeT, bladeRecess };

  return (
    <group>
      {/* Base */}
      <mesh position={[0, blockY + baseT / 2, 0]}>
        <RoundedBoxGeometry args={[blockW, baseT, blockD]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Coluna traseira esquerda */}
      <mesh position={[-blockW / 2 + colW / 2, blockY + baseT + bodyH / 2, -blockD / 2 + colW / 2]}>
        <RoundedBoxGeometry args={[colW, bodyH + colRadius * 4, colW]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Coluna traseira direita */}
      <mesh position={[blockW / 2 - colW / 2, blockY + baseT + bodyH / 2, -blockD / 2 + colW / 2]}>
        <RoundedBoxGeometry args={[colW, bodyH + colRadius * 4, colW]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Lâmina traseira */}
      <mesh position={[0, blockY + baseT + bodyH / 2, -blockD / 2 + bladeT / 1.08]}>
        <boxGeometry args={[blockW - 2 * colW, bodyH, bladeT]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Coluna frontal esquerda */}
      <mesh position={[-blockW / 2 + colW / 2, blockY + baseT + bodyH / 2, blockD / 2 - colW / 2]}>
        <RoundedBoxGeometry args={[colW, bodyH + colRadius * 4, colW]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Coluna frontal direita */}
      <mesh position={[blockW / 2 - colW / 2, blockY + baseT + bodyH / 2, blockD / 2 - colW / 2]}>
        <RoundedBoxGeometry args={[colW, bodyH + colRadius * 4, colW]} />
        <PlasticMat color={hex} />
      </mesh>

      {/* Lâminas coloridas */}
      {(['frente', 'esquerda', 'direita'] as PosicaoLamina[]).map((face) => {
        const lamina = config.laminas[face];
        if (!lamina.cor) return null;
        return (
          <Blade
            key={face}
            face={face}
            cor={COR_LAMINA_HEX[lamina.cor] ?? '#F0F0EE'}
            padrao={lamina.padrao}
            blockY={blockY}
            {...bladeProps}
          />
        );
      })}
    </group>
  );
}