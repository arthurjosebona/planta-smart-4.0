import { Expedicao } from '@entities/Expedicao';

export interface IExpedicaoRepository {
  findAll(): Promise<Expedicao[]>;
}
