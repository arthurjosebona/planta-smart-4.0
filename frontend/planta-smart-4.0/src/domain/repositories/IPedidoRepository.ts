import { Pedido } from '@entities/Pedido';

export interface IPedidoRepository {
  createPedido(pedido: Pedido): Pedido;
}
