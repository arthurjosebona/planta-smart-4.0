import { ConexaoRepository } from '@repositoriesImp/ConexaoRepository';
import { ModuloIP } from '@entities/ModuloIP';

export class ConexaoService {
  private readonly repository: ConexaoRepository;

  constructor(repository: ConexaoRepository) {
    this.repository = repository;
  }

  async conectar(modulos: ModuloIP[]): Promise<void> {
    return this.repository.conectar(modulos);
  }
}