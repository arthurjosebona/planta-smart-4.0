import { Expedicao } from '@entities/Expedicao';

export interface IExpedicaoRepository {
  findAll(): Promise<Expedicao[]>;
  updateAll(expedicao: Expedicao[]): Promise<void>;
}
