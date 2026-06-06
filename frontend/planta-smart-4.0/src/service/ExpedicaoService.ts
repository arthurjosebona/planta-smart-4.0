import { Expedicao } from '@entities/Expedicao';
import { ExpedicaoRepository } from '@repositoriesImp/ExpedicaoRepository';

export class ExpedicaoService {
  private readonly expedicaoRepository: ExpedicaoRepository;

  constructor(expedicaoRepository: ExpedicaoRepository) {
    this.expedicaoRepository = expedicaoRepository;
  }

  async findAll(): Promise<Expedicao[]> {
    return this.expedicaoRepository.findAll();
  }
}
