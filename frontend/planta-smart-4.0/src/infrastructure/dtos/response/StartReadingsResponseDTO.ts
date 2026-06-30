/**
 * Resposta de POST /api/smart/start-readings.
 * `resultados` mapeia o nome da estação → se o CLP conectou de fato;
 * `todasConectadas` é o agregado usado como fonte da verdade do status global.
 */
export interface StartReadingsResponseDTO {
  resultados: Record<string, boolean>;
  todasConectadas: boolean;
}
