export enum StatusPedido {
  Pendente,
  Producao,
  Concluido,
}

export const StatusPedidoToInt: Record<StatusPedido, number> = {
  [StatusPedido.Pendente]: 1,
  [StatusPedido.Producao]: 2,
  [StatusPedido.Concluido]: 3,
};
