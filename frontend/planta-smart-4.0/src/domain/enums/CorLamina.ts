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
