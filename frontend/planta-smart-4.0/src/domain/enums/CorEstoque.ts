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
