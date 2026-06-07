import { Pedido } from '@entities/Pedido';

export interface PedidosModel {
  pedidos: Pedido[];
  loading: boolean;
  erro: string | null;
  iniciarProducao: () => Promise<void>;
}

export const PedidosModelInitial: PedidosModel = {
  pedidos: [],
  loading: false,
  erro: null,
  iniciarProducao: async () => {},
};