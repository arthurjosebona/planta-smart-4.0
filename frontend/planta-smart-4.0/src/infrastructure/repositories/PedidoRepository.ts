import { Pedido } from '@entities/Pedido';
import { HttpClient } from '@http/HttpClient';
import { IPedidoRepository } from '@repositories/IPedidoRepository';
import { PedidoMapper } from '../mappers/PedidoMapper';

class PedidoRepository implements IPedidoRepository {
  private readonly httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  createPedido(pedido: Pedido): Pedido {
    const body = PedidoMapper.mapToCreateRequestDTO(pedido);
    // const data = await this.httpClient.post<PedidoResponseDTO>('/api/pedidos', body);
    // return PedidoMapper.toEntity(data);
  }
}
