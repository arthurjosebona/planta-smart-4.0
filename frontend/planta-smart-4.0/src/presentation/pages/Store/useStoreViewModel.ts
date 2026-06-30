import { useEffect, useState } from 'react';
import {
  StoreModel,
  StoreModelInitial,
  PedidoConfig,
  PEDIDO_CONFIG_CACHE_KEY,
  defaultPedidoConfig,
} from './StoreModel';
import { CorTampa } from '@enums/CorTampa';
import { CorBloco } from '@enums/CorBloco';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { CorLamina } from '@enums/CorLamina';
import { PadraoLamina } from '@enums/PadraoLamina';
import { ConfigBloco } from '@valueObjects/ConfigBloco';
import { pedidoService, cacheService } from '@config/diContainer';
import { Pedido } from '@entities/Pedido';
import { HttpError } from '@error/HttpError';

function loadInitialModel(): StoreModel {
  const cached = cacheService.get<PedidoConfig>(PEDIDO_CONFIG_CACHE_KEY);
  if (cached) {
    return { ...StoreModelInitial, ...cached };
  }
  return StoreModelInitial;
}

export function useStoreViewModel() {
  const [model, setModel] = useState<StoreModel>(loadInitialModel);

  // Persiste a config no cache sempre que os campos editáveis mudam.
  // Campos transientes (loading, erro, sucesso, pedidoCriado) são excluídos.
  useEffect(() => {
    const config: PedidoConfig = {
      ordemDeProducao: model.ordemDeProducao,
      numBlocos: model.numBlocos,
      corTampa: model.corTampa,
      blocos: model.blocos,
    };
    cacheService.set<PedidoConfig>(PEDIDO_CONFIG_CACHE_KEY, config);
  }, [model.ordemDeProducao, model.numBlocos, model.corTampa, model.blocos]);

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
      padrao: cor === null ? null : eraSemCor ? PadraoLamina.Nenhum : laminas[posicao].padrao,
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
    setModel((s) => ({ ...s, ordemDeProducao: n }));
  }

  function dismissFeedback() {
    setModel((s) => ({ ...s, erro: null, sucesso: false }));
  }

  async function createPedido() {
    // Itens em modo blueprint (cor === null) não podem ser enviados.
    const blocosVisiveis = model.blocos.slice(0, model.numBlocos);
    if (model.corTampa === null || blocosVisiveis.some((bloco) => bloco.cor === null)) {
      setModel((s) => ({
        ...s,
        erro: 'Escolha a cor da tampa e de todos os blocos antes de criar o pedido.',
      }));
      return;
    }

    setModel((s) => ({ ...s, loading: true, sucesso: false, erro: null }));
    try {
      const blocos = blocosVisiveis.map((bloco) => ({
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

      const blocosCompletos = [...blocos, ...Array(3 - blocos.length).fill({})] as [
        ConfigBloco,
        ConfigBloco,
        ConfigBloco,
      ];

      const pedido: Pedido = await pedidoService.create({
        ordemDeProducao: model.ordemDeProducao,
        numBlocos: model.numBlocos,
        blocos: blocosCompletos,
        corTampa: model.corTampa,
      });

      // Pedido criado → limpa cache e reseta o formulário para o estado padrão
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
    dismissFeedback,
  };
}
