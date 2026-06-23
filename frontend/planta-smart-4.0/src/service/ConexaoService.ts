import { ConexaoRepository } from '@repositoriesImp/ConexaoRepository';
import { ModuloIP } from '@entities/ModuloIP';
import { ClpPingResponseDTO } from '@dtos/response/ClpPingResponseDTO';
import { StartReadingsResponseDTO } from '@dtos/response/StartReadingsResponseDTO';

type Listener = () => void;

export class ConexaoService {
  private readonly repository: ConexaoRepository;
  private conectado = false;
  private readonly listeners = new Set<Listener>();

  constructor(repository: ConexaoRepository) {
    this.repository = repository;
  }

  /**
   * Grava os IPs configurados no backend (PUT /api/config/clp/ips).
   * NÃO define o status de conectado: persistir IPs não abre conexão com os CLPs.
   * O status só é determinado pelo resultado real de {@link iniciarLeituras}.
   */
  async conectar(modulos: ModuloIP[]): Promise<void> {
    try {
      await this.repository.conectar(modulos);
    } catch (error) {
      this.setConectado(false);
      throw error;
    }
  }

  /**
   * Inicia o loop de leitura dos CLPs no backend (POST /api/smart/start-readings),
   * o que passa a alimentar os streams SSE da bancada com dados em tempo real.
   * O status global de conectado reflete `todasConectadas` — só fica verde quando
   * TODAS as estações solicitadas responderam de fato.
   */
  async iniciarLeituras(modulos: ModuloIP[]): Promise<StartReadingsResponseDTO> {
    try {
      const resultado = await this.repository.iniciarLeituras(modulos);
      this.setConectado(resultado.todasConectadas);
      return resultado;
    } catch (error) {
      this.setConectado(false);
      throw error;
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