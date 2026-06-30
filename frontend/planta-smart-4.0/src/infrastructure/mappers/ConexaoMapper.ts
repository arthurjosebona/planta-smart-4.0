import { ModuloIP } from "@entities/ModuloIP";


export class ConexaoMapper {
  static mapToIpsMap(modulos: ModuloIP[]): Record<string, string> {
    return Object.fromEntries(modulos.map((m) => [m.key, m.ip]));
  }
}