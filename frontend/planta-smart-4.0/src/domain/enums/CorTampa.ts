export enum CorTampa {
  Preto = 'preto',
  Vermelho = 'vermelho',
  Azul = 'azul',
}

export const CorTampaToInt: Record<CorTampa, number> = {
  [CorTampa.Preto]: 1,
  [CorTampa.Vermelho]: 2,
  [CorTampa.Azul]: 3,
};
