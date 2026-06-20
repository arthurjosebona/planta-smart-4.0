import type {
  EstoqueStreamDTO,
  ProcessoMontagemStreamDTO,
  ExpedicaoStreamDTO,
} from '@entities/ClpStream';

export interface MonitorModel {
  estoque: EstoqueStreamDTO | null;
  processo: ProcessoMontagemStreamDTO | null;
  montagem: ProcessoMontagemStreamDTO | null;
  expedicao: ExpedicaoStreamDTO | null;
  conectado: boolean;
}

export const MonitorModelInitial: MonitorModel = {
  estoque: null,
  processo: null,
  montagem: null,
  expedicao: null,
  conectado: false,
};
