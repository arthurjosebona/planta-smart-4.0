import { Pedido } from '@entities/Pedido';

export interface IPedidoRepository {
  createPedido(pedido: Pedido): Promise<Pedido>;
  findAll(): Promise<Pedido[]>;
  findByOrdemDeProducao(op: number): Promise<Pedido>;
  iniciarProducao(id: number): Promise<Pedido>;
}
