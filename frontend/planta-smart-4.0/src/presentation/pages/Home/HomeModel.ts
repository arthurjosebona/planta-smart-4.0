import { ModuloIP } from '@entities/ModuloIP';
  
  export interface StoreModel {
    modulos: ModuloIP[];
    faixa: string;
    conectado: boolean;
    readOnly: boolean;
    loading: boolean;
    erro: string | null;
    sucesso: string | null;
  }
  
  export const StoreModelInitial: StoreModel = {
    modulos: [
      { key: 'estoque',   label: 'Estoque',   ip: '10.74.241.10' },
      { key: 'processo',  label: 'Processo',  ip: '10.74.241.20' },
      { key: 'montagem',  label: 'Montagem',  ip: '10.74.241.30' },
      { key: 'expedicao', label: 'Expedição', ip: '10.74.241.40' },
    ],
    faixa: '10.74.241.0',
    conectado: false,
    readOnly: false,
    loading: false,
    erro: null,
    sucesso: null,
  };