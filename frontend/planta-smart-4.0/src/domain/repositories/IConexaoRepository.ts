import { ModuloIP } from '@entities/ModuloIP';
import { ClpPingResponseDTO } from '@dtos/response/ClpPingResponseDTO';
import { StartReadingsResponseDTO } from '@dtos/response/StartReadingsResponseDTO';

export interface IConexaoRepository {
  conectar(modulos: ModuloIP[]): Promise<void>;
  iniciarLeituras(modulos: ModuloIP[]): Promise<StartReadingsResponseDTO>;
  desconectar(): Promise<void>;
  pingAll(): Promise<ClpPingResponseDTO[]>;
  setReadOnly(value: boolean): Promise<void>;
  getReadOnly(): Promise<boolean>;
}