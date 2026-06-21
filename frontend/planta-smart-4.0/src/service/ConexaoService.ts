import { ConexaoRepository } from '@repositoriesImp/ConexaoRepository';
import { ModuloIP } from '@entities/ModuloIP';
import { ClpPingResponseDTO } from '@dtos/response/ClpPingResponseDTO';

type Listener = () => void;

export class ConexaoService {
  private readonly repository: ConexaoRepository;
  private conectado = false;
  private readonly listeners = new Set<Listener>();

  constructor(repository: ConexaoRepository) {
    this.repository = repository;
  }

  async conectar(modulos: ModuloIP[]): Promise<void> {
    try {
      await this.repository.conectar(modulos);
      this.setConectado(true);
    } catch (error) {
      this.setConectado(false);
      throw error; // mantém o comportamento atual: quem chamou continua tratando o erro
    }
  }

  /** Snapshot atual do status — usado pelo useSyncExternalStore. */
  getSnapshot = (): boolean => {
    return this.conectado;
  };

  /** Inscreve um listener para mudanças de status; retorna a função de unsubscribe. */
  subscribe = (listener: Listener): (() => void) => {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  };

  async pingAll(): Promise<ClpPingResponseDTO[]> {
    return this.repository.pingAll();
  }

  private setConectado(value: boolean): void {
    if (this.conectado === value) return;
    this.conectado = value;
    this.listeners.forEach((listener) => listener());
  }
}