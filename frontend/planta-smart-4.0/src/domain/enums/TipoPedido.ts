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

export const IntToTipoPedido: Record<number, TipoPedido> = {
  1: TipoPedido.Simples,
  2: TipoPedido.Duplo,
  3: TipoPedido.Triplo,
};

export const TipoPedidoStringToEnum: Record<string, TipoPedido> = {
  SIMPLES: TipoPedido.Simples,
  DUPLO: TipoPedido.Duplo,
  TRIPLO: TipoPedido.Triplo,
};