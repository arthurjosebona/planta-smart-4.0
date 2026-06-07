export enum CorEstoque {
  Vazio,
  Preto,
  Vermelho,
  Azul,
}

export const CorEstoqueToInt: Record<CorEstoque, number> = {
  [CorEstoque.Vazio]: 0,
  [CorEstoque.Preto]: 1,
  [CorEstoque.Vermelho]: 2,
  [CorEstoque.Azul]: 3,
};

export const CorEstoqueStringToEnum: Record<string, CorEstoque> = {
  VAZIO: CorEstoque.Vazio,
  PRETO: CorEstoque.Preto,
  VERMELHO: CorEstoque.Vermelho,
  AZUL: CorEstoque.Azul,
};