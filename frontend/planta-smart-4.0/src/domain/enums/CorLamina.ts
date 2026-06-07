export enum CorLamina {
  Vermelho = 'vermelho',
  Azul = 'azul',
  Amarelo = 'amarelo',
  Verde = 'verde',
  Preto = 'preto',
  Branco = 'branco',
}

export const CorLaminaToInt: Record<CorLamina, number> = {
  [CorLamina.Vermelho]: 1,
  [CorLamina.Azul]: 2,
  [CorLamina.Amarelo]: 3,
  [CorLamina.Verde]: 4,
  [CorLamina.Preto]: 5,
  [CorLamina.Branco]: 6,
};

export const CorLaminaStringToEnum: Record<string, CorLamina> = {
  VERMELHO: CorLamina.Vermelho,
  AZUL: CorLamina.Azul,
  AMARELO: CorLamina.Amarelo,
  VERDE: CorLamina.Verde,
  PRETO: CorLamina.Preto,
  BRANCO: CorLamina.Branco,
};
