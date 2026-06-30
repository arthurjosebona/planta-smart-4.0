import { Estacao } from "./Estacao";

export enum EstacaoStatusModule {
    Desligado = "desligado",
    Aguardando = "aguardando",
    Finalizado = "finalizado",
    Ocupado = "ocupado"
}

export const IntToEstacaoStatusModule: Record<number, EstacaoStatusModule> = {
  0: EstacaoStatusModule.Aguardando,
  1: EstacaoStatusModule.Ocupado,
  2: EstacaoStatusModule.Finalizado
};
