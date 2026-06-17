import { PLASTIC_MATERIAL } from '@config/blockModel';

interface PlasticMatProps {
  color: string;
  /** Permite sobrescrever a rugosidade padrão (ex.: peças mais lisas). */
  roughness?: number;
  /** Permite sobrescrever o metalness padrão. */
  metalness?: number;
}

export function PlasticMat({
  color,
  roughness = PLASTIC_MATERIAL.roughness,
  metalness = PLASTIC_MATERIAL.metalness,
}: PlasticMatProps) {
  return <meshStandardMaterial color={color} roughness={roughness} metalness={metalness} />;
}
