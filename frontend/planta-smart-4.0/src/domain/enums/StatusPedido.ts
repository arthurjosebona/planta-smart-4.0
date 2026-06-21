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

export const StatusPedidoStringToEnum: Record<string, StatusPedido> = {
  PENDENTE: StatusPedido.Pendente,
  PRODUCAO: StatusPedido.Producao,
  CONCLUIDO: StatusPedido.Concluido,
};
