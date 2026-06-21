import { PosicaoLamina } from '@enums/PosicaoLamina';
import { CorTampa } from '@enums/CorTampa';
import { ConfigBloco } from 'src/domain/valueObjects/ConfigBloco';
import { Pedido } from '@entities/Pedido';

export interface StoreModel {
  ordemDeProducao: number;
  numBlocos: 1 | 2 | 3;
  /** `null` enquanto a tampa está no modo blueprint (cor ainda não escolhida). */
  corTampa: CorTampa | null;
  blocos: [ConfigBloco, ConfigBloco, ConfigBloco]; // sempre 3, numBlocos decide quantos renderizar
  loading: boolean;
  erro: string | null;
  sucesso: boolean;
  pedidoCriado: Pedido | null;
}

const createDefaultBlock = (): ConfigBloco => {
  return {
    cor: null,
    laminas: {
      [PosicaoLamina.Direita]: {
        cor: null,
        padrao: null,
      },
      [PosicaoLamina.Esquerda]: {
        cor: null,
        padrao: null,
      },
      [PosicaoLamina.Frente]: {
        cor: null,
        padrao: null,
      },
    },
  };
};

export const StoreModelInitial: StoreModel = {
  ordemDeProducao: 1,
  numBlocos: 1,
  corTampa: null,
  blocos: [createDefaultBlock(), createDefaultBlock(), createDefaultBlock()],
  loading: false,
  erro: null,
  sucesso: false,
  pedidoCriado: null,
};


export interface PedidoConfig {
  ordemDeProducao: number;
  numBlocos: 1 | 2 | 3;
  corTampa: CorTampa | null;
  blocos: [ConfigBloco, ConfigBloco, ConfigBloco];
}

export const PEDIDO_CONFIG_CACHE_KEY = 'pedido_config';

export const defaultPedidoConfig: PedidoConfig = {
  ordemDeProducao: 1,
  numBlocos: 1,
  corTampa: null,
  blocos: [createDefaultBlock(), createDefaultBlock(), createDefaultBlock()],
};
