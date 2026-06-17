import { ModuloIP } from '@entities/ModuloIP';

export interface IConexaoRepository {
  conectar(modulos: ModuloIP[]): Promise<void>;
}