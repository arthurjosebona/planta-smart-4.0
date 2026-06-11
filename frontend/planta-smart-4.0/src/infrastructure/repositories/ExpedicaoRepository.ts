import { HttpClient } from '@http/HttpClient';
import { ExpedicaoResponseDTO } from '@dtos/response/ExpedicaoResponseDTO';
import { ExpedicaoMapper } from '../mappers/ExpedicaoMapper';
import { Expedicao } from '@entities/Expedicao';
import { IExpedicaoRepository } from '@repositories/IExpedicaoRepository';
import { ExpedicaoRequestDTO } from '@dtos/request/ExpedicaoRequestDTO';

export class ExpedicaoRepository implements IExpedicaoRepository {
  private readonly httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  async findAll(): Promise<Expedicao[]> {
    const data: ExpedicaoResponseDTO[] =
      await this.httpClient.get<ExpedicaoResponseDTO[]>('/api/expedicao');
    return ExpedicaoMapper.mapEntitiesByResponsesDTOs(data);
  }

  async updateAll(expedicao: Expedicao[]): Promise<void> {
    const request: ExpedicaoRequestDTO[] = 
      ExpedicaoMapper.mapRequestDTOsByEntities(expedicao);
    await this.httpClient.put<void>('/api/expedicao', request);
  }
}
