export enum PosicaoLamina {
  Esquerda = 'esquerda',
  Frente = 'frente',
  Direita = 'direita',
}

export const PosicaoLaminaToInt: Record<PosicaoLamina, number> = {
  [PosicaoLamina.Esquerda]: 1,
  [PosicaoLamina.Frente]: 2,
  [PosicaoLamina.Direita]: 3,
};
