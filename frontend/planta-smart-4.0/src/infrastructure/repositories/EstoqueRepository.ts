import { EstoqueResponseDTO } from '@dtos/response/EstoqueResponseDTO';
import { Estoque } from '@entities/Estoque';
import { HttpClient } from '@http/HttpClient';
import { IEstoqueRepository } from '@repositories/IEstoqueRepository';
import { EstoqueMapper } from '../mappers/EstoqueMapper';

export class EstoqueRepository implements IEstoqueRepository {
  private readonly httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  async findAll(): Promise<Estoque[]> {
    const data: EstoqueResponseDTO[] =
      await this.httpClient.get<EstoqueResponseDTO[]>('/api/estoque');
    console.log(JSON.stringify(EstoqueMapper.mapEntitiesByResponsesDTOs(data)));
    return EstoqueMapper.mapEntitiesByResponsesDTOs(data);
  }

  async updateAll(estoque: Estoque[]): Promise<void> {
    const body = EstoqueMapper.mapRequestsDTOByEntities(estoque);
    console.log('Enviando para a API: ' + JSON.stringify(body));
    await this.httpClient.put('/api/estoque', body);
  }
}
