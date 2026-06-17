import { Pedido } from '@entities/Pedido';
import { HttpClient } from '@http/HttpClient';
import { IPedidoRepository } from '@repositories/IPedidoRepository';
import { PedidoMapper } from '../mappers/PedidoMapper';
import { PedidoCreateResponseDTO } from '@dtos/response/PedidoCreateResponseDTO';
import { PedidoGetResponseDTO } from '@dtos/response/PedidoGetResponseDTO';

export class PedidoRepository implements IPedidoRepository {
  private readonly httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  async createPedido(pedido: Pedido): Promise<Pedido> {
    const body = PedidoMapper.mapToCreateRequestDTO(pedido);
    const data: PedidoCreateResponseDTO = await this.httpClient.post<PedidoCreateResponseDTO>(
      '/api/pedidos',
      body
    );
    return PedidoMapper.mapToEntityByCreateDTO(data);
  }

  async findAll(): Promise<Pedido[]> {
    const data: PedidoGetResponseDTO[] =
      await this.httpClient.get<PedidoGetResponseDTO[]>('/api/pedidos');
    return PedidoMapper.mapToEntitiesByGetDTOs(data);
  }

  async findById(id: number): Promise<Pedido> {
    const data: PedidoGetResponseDTO = await this.httpClient.get<PedidoGetResponseDTO>(
      '/api/pedidos/' + id
    );
    return PedidoMapper.mapToEntityByGetDTO(data);
  }

  async iniciarProducao(id: number): Promise<Pedido> {
    const data: PedidoGetResponseDTO = await this.httpClient.put(
      '/api/pedidos/' + id + '/status',
      {}
    );
    return PedidoMapper.mapToEntityByGetDTO(data);
  }

  async update(id: number, pedido: Pedido): Promise<Pedido> {
    const body = PedidoMapper.mapToCreateRequestDTO(pedido);
    const data: PedidoGetResponseDTO = await this.httpClient.put<PedidoGetResponseDTO>(
      '/api/pedidos/' + id,
      body
    );
    return PedidoMapper.mapToEntityByGetDTO(data);
  }

  async delete(id: number): Promise<void> {
    await this.httpClient.delete<void>('/api/pedidos/' + id);
  }
}