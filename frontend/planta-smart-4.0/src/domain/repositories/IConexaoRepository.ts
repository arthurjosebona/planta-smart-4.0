import { ModuloIP } from '@entities/ModuloIP';
import { ClpPingResponseDTO } from '@dtos/response/ClpPingResponseDTO';

export interface IConexaoRepository {
  conectar(modulos: ModuloIP[]): Promise<void>;
  pingAll(): Promise<ClpPingResponseDTO[]>;
}