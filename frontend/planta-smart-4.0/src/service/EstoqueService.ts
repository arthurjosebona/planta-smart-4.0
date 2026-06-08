import { Estoque } from '@entities/Estoque';
import { EstoqueRepository } from '@repositoriesImp/EstoqueRepository';

export class EstoqueService {
  private readonly estoqueRepository: EstoqueRepository;

  constructor(estoqueRepository: EstoqueRepository) {
    this.estoqueRepository = estoqueRepository;
  }

  async findAll(): Promise<Estoque[]> {
    return this.estoqueRepository.findAll();
  }

  async updateAll(estoque: Estoque[]): Promise<void> {
    return this.estoqueRepository.updateAll(estoque);
  }
}
