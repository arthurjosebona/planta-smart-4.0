import { Pedido } from '@entities/Pedido';
import { StatusPedido } from '@enums/StatusPedido';

export interface PedidosModel {
  pedidos: Pedido[];
  loading: boolean;
  erro: string | null;
  filtroStatus: StatusPedido | null;
  iniciarProducao: (id: number) => Promise<void>;
}

export const PedidosModelInitial: PedidosModel = {
  pedidos: [],
  loading: false,
  erro: null,
  filtroStatus: null,
  iniciarProducao: async (id: number) => {},
};
