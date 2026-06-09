// useStoreViewModel.ts
import { useState } from 'react';
import {
  StoreModel,
  StoreModelInitial,
  PedidoConfig,
  PEDIDO_CONFIG_CACHE_KEY,
  defaultPedidoConfig,
} from './StoreModel';
import { CorTampa }      from '@enums/CorTampa';
import { CorBloco }      from '@enums/CorBloco';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { CorLamina }     from '@enums/CorLamina';
import { PadraoLamina }  from '@enums/PadraoLamina';
import { ConfigBloco }   from '@valueObjects/ConfigBloco';
import { pedidoService } from '@config/diContainer';
import { Pedido }        from '@entities/Pedido';
import { HttpError }     from '@error/HttpError';
import { cacheService } from '@config/diContainer';

const PEDIDO_CONFIG_TTL = 1000 * 60 * 15; // 15 min

function loadInitialModel(): StoreModel {
  const cached = cacheService.get<PedidoConfig>(PEDIDO_CONFIG_CACHE_KEY);
  return cached
    ? { ...StoreModelInitial, ...cached }
    : StoreModelInitial;
}

function persistConfig(model: StoreModel): void {
  const { ordemDeProducao, numBlocos, corTampa, blocos } = model;
  cacheService.set<PedidoConfig>(
    PEDIDO_CONFIG_CACHE_KEY,
    { ordemDeProducao, numBlocos, corTampa, blocos },
    { ttl: PEDIDO_CONFIG_TTL }
  );
}

export function useStoreViewModel() {
  const [model, setModel] = useState<StoreModel>(loadInitialModel);

  function update(updater: (s: StoreModel) => StoreModel) {
    setModel((s) => {
      const next = updater(s);
      persistConfig(next);
      return next;
    });
  }

  function setNumBlocos(n: 1 | 2 | 3) {
    update((s) => ({ ...s, numBlocos: n }));
  }

  function setCorTampa(cor: CorTampa) {
    update((s) => ({ ...s, corTampa: cor }));
  }

  function setBlocoField(idx: number, updates: Partial<ConfigBloco>) {
    update((s) => {
      const blocos = [...s.blocos] as typeof s.blocos;
      blocos[idx] = { ...blocos[idx], ...updates };
      return { ...s, blocos };
    });
  }

  function setBlocoColor(idx: number, cor: CorBloco) {
    setBlocoField(idx, { cor });
  }

  function setLaminaCor(idx: number, posicao: PosicaoLamina, cor: CorLamina | null) {
    update((s) => {
      const blocos = [...s.blocos] as typeof s.blocos;
      const laminas = { ...blocos[idx].laminas };
      const eraSemCor = laminas[posicao].cor === null;

      laminas[posicao] = {
        cor,
        padrao: cor === null ? null : eraSemCor ? PadraoLamina.Nenhum : laminas[posicao].padrao,
      };

      blocos[idx] = { ...blocos[idx], laminas };
      return { ...s, blocos };
    });
  }

  function setLaminaPadrao(idx: number, posicao: PosicaoLamina, padrao: PadraoLamina | null) {
    update((s) => {
      const blocos = [...s.blocos] as typeof s.blocos;
      const laminas = { ...blocos[idx].laminas };
      laminas[posicao] = { ...laminas[posicao], padrao };
      blocos[idx] = { ...blocos[idx], laminas };
      return { ...s, blocos };
    });
  }

  function setOrdemDeProducao(n: number) {
    update((s) => ({ ...s, ordemDeProducao: n }));
  }

  async function createPedido() {
    setModel((s) => ({ ...s, loading: true, sucesso: false, erro: null }));
    try {
      const blocos = model.blocos.slice(0, model.numBlocos).map((bloco) => ({
        ...bloco,
        laminas: Object.fromEntries(
          Object.entries(bloco.laminas)
            .filter(([, lamina]) => lamina.cor != null)
            .map(([posicao, lamina]) => [
              posicao,
              { cor: lamina.cor, padrao: lamina.padrao ?? PadraoLamina.Nenhum },
            ])
        ),
      }));

      const blocosCompletos = [...blocos, ...Array(3 - blocos.length).fill({})] as [
        ConfigBloco, ConfigBloco, ConfigBloco,
      ];

      const pedido: Pedido = await pedidoService.create({
        ordemDeProducao: model.ordemDeProducao,
        numBlocos: model.numBlocos,
        blocos: blocosCompletos,
        corTampa: model.corTampa,
      });

      // ✅ Sucesso: limpa cache e reseta config para o padrão
      cacheService.clear(PEDIDO_CONFIG_CACHE_KEY);
      setModel({
        ...StoreModelInitial,
        ...defaultPedidoConfig,
        loading: false,
        sucesso: true,
        erro: null,
        pedidoCriado: pedido,
      });
    } catch (error: unknown) {
      const mensagem = error instanceof HttpError ? error.message : 'Erro desconhecido';
      setModel((s) => ({ ...s, loading: false, sucesso: false, erro: mensagem }));
      // ✅ Falha: cache é mantido — usuário não perde o que configurou
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
    createPedido,
  };
}