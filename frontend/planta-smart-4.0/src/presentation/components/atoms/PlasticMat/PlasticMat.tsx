interface PlasticMatProps {
  color: string;
}

export function PlasticMat({ color }: PlasticMatProps) {
  return <meshStandardMaterial color={color} roughness={0.55} metalness={0.0} />;
}