export type ModuleStatus = 'off' | 'pause' | 'on';

export interface BancadaStatus {
  estoque: ModuleStatus;
  processo: ModuleStatus;
  montagem: ModuleStatus;
  expedicao: ModuleStatus;
}

export const BANCADA_STATUS_DEFAULT: BancadaStatus = {
  estoque: 'off',
  processo: 'off',
  montagem: 'off',
  expedicao: 'off',
};
