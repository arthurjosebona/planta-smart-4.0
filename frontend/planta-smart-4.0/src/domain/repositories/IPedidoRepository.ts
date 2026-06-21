import { Pedido } from '@entities/Pedido';

export interface IPedidoRepository {
  createPedido(pedido: Pedido): Promise<Pedido>;
  findAll(): Promise<Pedido[]>;
  findById(id: number): Promise<Pedido>;
  findByOrdemDeProducao(op: number): Promise<Pedido>;
  findByExpedicao(expedicaoId: number): Promise<Pedido[]>;
  iniciarProducao(id: number): Promise<Pedido>;
  update(id: number, pedido: Pedido): Promise<Pedido>;
  delete(id: number): Promise<void>;
}