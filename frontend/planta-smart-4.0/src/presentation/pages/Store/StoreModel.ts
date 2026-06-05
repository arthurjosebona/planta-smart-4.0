import { CorBloco } from '../../../domain/enums/CorBloco';
import { PosicaoLamina } from '../../../domain/enums/PosicaoLamina';
import { CorTampa } from '../../../domain/enums/CorTampa';
import { ConfigBloco } from '../../../domain/entities/ConfigBloco';

export interface StoreModel {
  numBlocos: 1 | 2 | 3;
  corTampa: CorTampa;
  blocos: [ConfigBloco, ConfigBloco, ConfigBloco]; // sempre 3, numBlocos decide quantos renderizar
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
  numBlocos: 1,
  corTampa: CorTampa.Azul,
  blocos: [createDefaultBlock(), createDefaultBlock(), createDefaultBlock()],
};
