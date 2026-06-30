import { EstoqueStream } from "@entities/stream/EstoqueStream";
import { ExpedicaoStream } from "@entities/stream/ExpedicaoStream";
import { ProcessoMontagemStream } from "@entities/stream/ProcessoMontagemStream";

export interface MonitorModel {
  estoque: EstoqueStream | null;
  processo: ProcessoMontagemStream | null;
  montagem: ProcessoMontagemStream | null;
  expedicao: ExpedicaoStream | null;
  conectado: boolean;
}

export const MonitorModelInitial: MonitorModel = {
  estoque: null,
  processo: null,
  montagem: null,
  expedicao: null,
  conectado: false,
};
