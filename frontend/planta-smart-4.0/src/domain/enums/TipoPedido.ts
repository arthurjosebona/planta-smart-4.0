export enum TipoPedido {
  Simples,
  Duplo,
  Triplo,
}

export const TipoPedidoToInt: Record<TipoPedido, number> = {
  [TipoPedido.Simples]: 1,
  [TipoPedido.Duplo]: 2,
  [TipoPedido.Triplo]: 3,
};
