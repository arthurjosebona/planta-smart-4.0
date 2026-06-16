import { Pedido } from '@entities/Pedido';
import { Bloco } from '@entities/Bloco';
import { StoreModel, StoreModelInitial } from '@pages/Store/StoreModel';
import { ConfigBloco } from '@valueObjects/ConfigBloco';
import { ConfigLamina } from '@valueObjects/ConfigLamina';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { CorBloco } from '@enums/CorBloco';

function createEmptyLaminas(): Record<PosicaoLamina, ConfigLamina> {
  return {
    [PosicaoLamina.Esquerda]: { cor: null, padrao: null },
    [PosicaoLamina.Frente]: { cor: null, padrao: null },
    [PosicaoLamina.Direita]: { cor: null, padrao: null },
  };
}

function blocoToConfigBloco(bloco: Bloco): ConfigBloco {
  const laminas = createEmptyLaminas();

  for (const lamina of bloco.laminas) {
    laminas[lamina.posicao] = { cor: lamina.cor, padrao: lamina.padrao };
  }

  return { cor: bloco.cor, laminas };
}

const createDefaultConfigBloco = (): ConfigBloco => ({
  cor: CorBloco.Azul,
  laminas: createEmptyLaminas(),
});

export function pedidoToStoreModel(pedido: Pedido): StoreModel {
  const blocosOrdenados = [...pedido.blocos].sort((a, b) => a.andar - b.andar);

  const blocos: [ConfigBloco, ConfigBloco, ConfigBloco] = [
    createDefaultConfigBloco(),
    createDefaultConfigBloco(),
    createDefaultConfigBloco(),
  ];

  blocosOrdenados.slice(0, 3).forEach((bloco, i) => {
    blocos[i] = blocoToConfigBloco(bloco);
  });

  const numBlocos = (Math.min(Math.max(blocosOrdenados.length, 1), 3) as 1 | 2 | 3);

  return {
    ...StoreModelInitial,
    ordemDeProducao: pedido.ordemDeProducao,
    numBlocos,
    corTampa: pedido.corTampa,
    blocos,
  };
}
