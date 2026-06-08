import { Estoque } from '@entities/Estoque';

export interface IEstoqueRepository {
  findAll(): Promise<Estoque[]>;
  updateAll(estoque: Estoque[]): Promise<void>;
}
