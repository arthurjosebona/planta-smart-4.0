import { CorBloco } from '@enums/CorBloco';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { CorTampa } from '@enums/CorTampa';
import { ConfigBloco } from 'src/domain/valueObjects/ConfigBloco';
import { Pedido } from '@entities/Pedido';

export interface StoreModel {
  ordemDeProducao: number;
  numBlocos: 1 | 2 | 3;
  corTampa: CorTampa;
  blocos: [ConfigBloco, ConfigBloco, ConfigBloco]; // sempre 3, numBlocos decide quantos renderizar
  loading: boolean;
  erro: string | null;
  sucesso: boolean;
  pedidoCriado: Pedido | null;
}

const createDefaultBlock = (): ConfigBloco => {
  return {
    cor: CorBloco.Azul,
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
  corTampa: CorTampa.Azul,
  blocos: [createDefaultBlock(), createDefaultBlock(), createDefaultBlock()],
  loading: false,
  erro: null,
  sucesso: false,
  pedidoCriado: null,
};
