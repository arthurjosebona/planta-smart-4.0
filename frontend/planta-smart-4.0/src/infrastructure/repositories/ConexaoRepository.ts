import { HttpClient } from '@http/HttpClient';
import { IConexaoRepository } from '@repositories/IConexaoRepository';
import { ModuloIP } from '@entities/ModuloIP';
import { ClpPingResponseDTO } from '@dtos/response/ClpPingResponseDTO';
import { ClpReadOnlyResponseDTO } from '@dtos/response/ClpReadOnlyResponseDTO';
import { StartReadingsResponseDTO } from '@dtos/response/StartReadingsResponseDTO';
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

  async iniciarLeituras(modulos: ModuloIP[]): Promise<StartReadingsResponseDTO> {
    // Rota: POST /api/smart/start-readings — inicia o loop de leitura dos CLPs,
    // alimentando os streams SSE da bancada. Body: { estoque, processo, montagem, expedicao }.
    // Responde com o resultado real da conexão por estação (ver StartReadingsResponseDTO).
    const body = ConexaoMapper.mapToIpsMap(modulos);
    return this.httpClient.post<StartReadingsResponseDTO>('/api/smart/start-readings', body);
  }

  async pingAll(): Promise<ClpPingResponseDTO[]> {
    // Rota: POST /api/smart/ping — sem body
    return this.httpClient.post<ClpPingResponseDTO[]>('/api/smart/ping', {});
  }

  async setReadOnly(value: boolean): Promise<void> {
    // Rota: POST /api/smart/readonly?value=true|false — valor via query param, sem body
    await this.httpClient.post<void>(`/api/smart/readonly?value=${value}`, {});
  }

  async getReadOnly(): Promise<boolean> {
    // Rota: GET /api/smart/readonly → { readOnly: boolean }
    const dto = await this.httpClient.get<ClpReadOnlyResponseDTO>('/api/smart/readonly');
    return dto.readOnly;
  }
}