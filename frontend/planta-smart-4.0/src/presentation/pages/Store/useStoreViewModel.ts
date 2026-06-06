import { useState } from 'react';
import { StoreModel, StoreModelInitial } from './StoreModel';
import { CorTampa } from '@enums/CorTampa';
import { CorBloco } from '@enums/CorBloco';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { CorLamina } from '@enums/CorLamina';
import { PadraoLamina } from '@enums/PadraoLamina';
import { ConfigBloco } from '@valueObjects/ConfigBloco';
import { pedidoService } from '@config/diContainer';
import { Pedido } from '@entities/Pedido';
import { HttpError } from '@error/HttpError';

export function useStoreViewModel() {
  const [model, setModel] = useState<StoreModel>(StoreModelInitial);

  function setNumBlocos(n: 1 | 2 | 3) {
    setModel((s) => ({ ...s, numBlocos: n }));
  }

  function setCorTampa(cor: CorTampa) {
    setModel((s) => ({ ...s, corTampa: cor }));
  }

  function setBlocoField(idx: number, updates: Partial<ConfigBloco>) {
    const blocos = [...model.blocos] as typeof model.blocos;
    blocos[idx] = { ...blocos[idx], ...updates };
    setModel((s) => ({ ...s, blocos }));
  }

  function setBlocoColor(idx: number, cor: CorBloco) {
    setBlocoField(idx, { cor });
  }

  function setLaminaCor(idx: number, posicao: PosicaoLamina, cor: CorLamina | null) {
    const blocos = [...model.blocos] as typeof model.blocos;
    const laminas = { ...blocos[idx].laminas };
    const eraSemCor = laminas[posicao].cor === null;

    laminas[posicao] = {
      cor,
      padrao: cor === null
        ? null
        : eraSemCor
          ? PadraoLamina.Nenhum
          : laminas[posicao].padrao,
    };

    blocos[idx] = { ...blocos[idx], laminas };
    setModel((s) => ({ ...s, blocos }));
  }

  function setLaminaPadrao(idx: number, posicao: PosicaoLamina, padrao: PadraoLamina | null) {
    const blocos = [...model.blocos] as typeof model.blocos;
    const laminas = { ...blocos[idx].laminas };
    laminas[posicao] = { ...laminas[posicao], padrao };
    blocos[idx] = { ...blocos[idx], laminas };
    setModel((s) => ({ ...s, blocos }));
  }

  function setOrdemDeProducao(n: number) {
    setModel((s) => ({ ...s, ordemDeProducao:n }));
  }

  async function createPedido() {
    setModel((s) => ({ ...s, loading:true, sucesso:false, erro:null }));
    try {
      console.log("blocos raw:", JSON.stringify(model.blocos.slice(0, model.numBlocos), null, 2));
      const blocos = model.blocos.slice(0, model.numBlocos).map((bloco) => ({
        ...bloco,
        laminas: Object.fromEntries(
          Object.entries(bloco.laminas)
          .filter(([, lamina]) => lamina.cor != null)  
          .map(([posicao, lamina]) => [
            posicao,
            {
              cor: lamina.cor,
              padrao: lamina.padrao ?? PadraoLamina.Nenhum,
            },
          ])
        ),
      }));

      const blocosCompletos = [
        ...blocos,
        ...Array(3 - blocos.length).fill({}),
      ] as [ConfigBloco, ConfigBloco, ConfigBloco];

      const pedido: Pedido = await pedidoService.create({ 
        ordemDeProducao: model.ordemDeProducao,
        numBlocos: model.numBlocos,
        blocos: blocosCompletos,
        corTampa: model.corTampa
      });

      setModel((s) => ({ ...s, loading: false, sucesso: true, erro: null, pedidoCriado: pedido }));
    } catch (error: unknown) {
      const mensagem =
        error instanceof HttpError
          ? error.message       
          : 'Erro desconhecido';

      setModel((s) => ({
        ...s,
        loading: false,
        sucesso: false,
        erro: mensagem,
      }));
    }
  }

  return {
    model,
    setNumBlocos,
    setCorTampa,
    setBlocoField,
    setBlocoColor,
    setLaminaCor,
    setLaminaPadrao,
    setOrdemDeProducao,
    createPedido
  };
}
