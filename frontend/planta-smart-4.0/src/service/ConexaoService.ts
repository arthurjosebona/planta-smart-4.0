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

  /**
   * Inicia o loop de leitura dos CLPs no backend (POST /api/smart/start-readings),
   * o que passa a alimentar os streams SSE da bancada com dados em tempo real.
   */
  async iniciarLeituras(modulos: ModuloIP[]): Promise<void> {
    return this.repository.iniciarLeituras(modulos);
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

  async setReadOnly(value: boolean): Promise<void> {
    return this.repository.setReadOnly(value);
  }

  async getReadOnly(): Promise<boolean> {
    return this.repository.getReadOnly();
  }

  private setConectado(value: boolean): void {
    if (this.conectado === value) return;
    this.conectado = value;
    this.listeners.forEach((listener) => listener());
  }
}