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

export const CorTampaStringToEnum: Record<string, CorTampa> = {
  PRETO: CorTampa.Preto,
  VERMELHO: CorTampa.Vermelho,
  AZUL: CorTampa.Azul,
};