import { Pedido } from '@entities/Pedido';
import { HttpClient } from '@http/HttpClient';
import { IPedidoRepository } from '@repositories/IPedidoRepository';
import { PedidoMapper } from '../mappers/PedidoMapper';
import { PedidoCreateResponseDTO } from '@dtos/response/PedidoCreateResponseDTO';
import { PedidoGetResponseDTO } from '@dtos/response/PedidoGetResponseDTO';
import { FilaProducao } from '@entities/FilaProducao';
import { FilaStreamDTO } from '@dtos/response/stream/FilaStreamDTO';
import { FilaProducaoMapper } from '../mappers/FilaProducaoMapper';

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
    
  async findByOrdemDeProducao(op: number): Promise<Pedido> {
    const data: PedidoGetResponseDTO = await this.httpClient.get<PedidoGetResponseDTO>(
      `/api/pedidos/op/${op}`
    );
    return PedidoMapper.mapToEntityByGetDTO(data);
  }

  async findByExpedicao(expedicaoId: number): Promise<Pedido[]> {
    const data: PedidoGetResponseDTO[] = await this.httpClient.get<PedidoGetResponseDTO[]>(
      `/api/pedidos/expedicao/${expedicaoId}`
    );
    return PedidoMapper.mapToEntitiesByGetDTOs(data);
  }

  async iniciarProducao(id: number): Promise<Pedido> {
    const data: PedidoGetResponseDTO = await this.httpClient.put(
      '/api/pedidos/start-production/' + id,
      {}
    );
    console.log(PedidoMapper.mapToEntityByGetDTO(data))
    return PedidoMapper.mapToEntityByGetDTO(data);
  }

  async enviarParaProducao(id: number): Promise<FilaProducao> {
    const data: FilaStreamDTO = await this.httpClient.post<FilaStreamDTO>(
      `/api/pedidos/${id}/enviar-producao`,
      {}
    );
    return FilaProducaoMapper.mapToEntityByStreamDTO(data);
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