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

export const PosicaoLaminaStringToEnum: Record<string, PosicaoLamina> = {
  ESQUERDA: PosicaoLamina.Esquerda,
  FRENTE: PosicaoLamina.Frente,
  DIREITA: PosicaoLamina.Direita,
};
