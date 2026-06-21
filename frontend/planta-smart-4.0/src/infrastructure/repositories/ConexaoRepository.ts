import { HttpClient } from '@http/HttpClient';
import { IConexaoRepository } from '@repositories/IConexaoRepository';
import { ModuloIP } from '@entities/ModuloIP';
import { ClpPingResponseDTO } from '@dtos/response/ClpPingResponseDTO';
import { ConexaoMapper } from '../mappers/ConexaoMapper';

export class ConexaoRepository implements IConexaoRepository {
  private readonly httpClient: HttpClient;

  constructor(httpClient: HttpClient) {
    this.httpClient = httpClient;
  }

  async conectar(modulos: ModuloIP[]): Promise<void> {
    const body = ConexaoMapper.mapToIpsMap(modulos);
    await this.httpClient.put<void>('/api/config/clp/ips', body);
  }

  async pingAll(): Promise<ClpPingResponseDTO[]> {
    // Rota: POST /api/smart/smart/ping — sem body
    return this.httpClient.post<ClpPingResponseDTO[]>('/api/smart/smart/ping', {});
  }
}