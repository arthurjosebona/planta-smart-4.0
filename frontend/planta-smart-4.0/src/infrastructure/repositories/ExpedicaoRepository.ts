import { HttpClient } from '@http/HttpClient';
import { ExpedicaoResponseDTO } from '@dtos/response/ExpedicaoResponseDTO';
import { ExpedicaoMapper } from '../mappers/ExpedicaoMapper';
import { Expedicao } from '@entities/Expedicao';
import { IExpedicaoRepository } from '@repositories/IExpedicaoRepository';

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
}
