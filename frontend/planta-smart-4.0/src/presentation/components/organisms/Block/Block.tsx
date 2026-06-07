import { RoundedBoxGeometry } from '@react-three/drei';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { ConfigBloco } from '@valueObjects/ConfigBloco';
import { PlasticMat } from '@components/atoms/PlasticMat/PlasticMat';
import { Blade } from '@components/molecules/Blade/Blade';

// ─── Mapas de cor ─────────────────────────────────────────────────────────────

const COR_BLOCO_HEX: Record<string, string> = {
  preto: '#484848',
  vermelho: '#e6463f',
  azul: '#0065d7',
};

const COR_LAMINA_HEX: Record<string, string> = {
  vermelho: '#e6463f',
  azul: '#1a55cc',
  amarelo: '#e6b800',
  verde: '#229944',
  preto: '#484848',
  branco: '#f0f0ee',
};

// ─── Block ────────────────────────────────────────────────────────────────────
// Organism: base + colunas + lâmina traseira + lâminas coloridas.

interface BlockProps {
  config: ConfigBloco;
  blockY: number;
  // dimensões recebidas do BlockScene pai
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
  const hex = COR_BLOCO_HEX[config.cor];
  const bodyH = blockH - baseT;

  // props de dimensão compartilhadas com os filhos Blade
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
            cor={COR_LAMINA_HEX[lamina.cor]}
            padrao={lamina.padrao}
            blockY={blockY}
            {...bladeProps}
          />
        );
      })}
    </group>
  );
}
