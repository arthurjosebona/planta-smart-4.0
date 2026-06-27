import { useEstoqueContext } from '@contexts/EstoqueContext';
import { useExpedicaoContext } from '@contexts/ExpedicaoContext';
import { useMonitorContext } from '@contexts/MonitorContext';
import { usePingContext } from '@contexts/PingContext';
import { StatusEstacao } from '@enums/StatusEstacao';
import { CorEstoque } from '@enums/CorEstoque';
import type { Estoque } from '@entities/Estoque';
import type { Expedicao } from '@entities/Expedicao';
import { useEffect, useState } from 'react';
import { Estacao } from '@enums/Estacao';
import { EstacaoStatusModule } from '@enums/EstacaoStatusModule';
import { EstacaoStatusPipe } from '@enums/EstacaoStatusPipe';

// Converte o vetor de cores do magazine de estoque recebido via SSE
// (posicoesOcupadas) em entidades Estoque para exibição somente-leitura.
// O índice do vetor corresponde à posição física (1-based) e o valor à cor (0..3).
function mapBancadaEstoque(posicoesOcupadas: number[] | undefined): Estoque[] {
  return (posicoesOcupadas ?? []).map((cor, index) => ({
    id: index + 1,
    posicaoFisica: index + 1,
    cor: (cor >= 0 && cor <= 3 ? cor : 0) as CorEstoque,
  }));
}

// Converte o vetor de ordens de produção do magazine de expedição recebido via SSE
// (orderExpedicao) em entidades Expedicao. Valor 0 indica posição livre (exibida vazia).
function mapBancadaExpedicao(orderExpedicao: number[] | undefined): Expedicao[] {
  return (orderExpedicao ?? []).map((op, index) => ({
    id: index + 1,
    posicaoFisica: index + 1,
    ordemDeProducaoAtual: (op === 0 ? null : op) as unknown as number,
  }));
}

function useLatch(trigger: boolean | undefined, reset: boolean | undefined): boolean {
  const [latched, setLatched] = useState(false);

  // Liga o latch quando a flag de início dispara
  useEffect(() => {
    if (trigger) setLatched(true);
  }, [trigger]);

  // Desliga o latch quando a condição de reset ocorre
  useEffect(() => {
    if (reset) setLatched(false);
  }, [reset]);

  return latched;
}

export function useEstacoesViewModel() {
  const estoque = useEstoqueContext();
  const expedicao = useExpedicaoContext();
  const monitor = useMonitorContext();
  const { pingMap } = usePingContext();

  function dismissErro() {
    estoque.dismissErro();
    expedicao.dismissErro();
  }

  // Conteúdo físico das magazines da bancada, derivado do stream SSE (MonitorContext).
  const bancada = {
    estoque: mapBancadaEstoque(monitor.estoque?.posicoesOcupadas),
    expedicao: mapBancadaExpedicao(monitor.expedicao?.orderExpedicao),
  };

  const finalizadoEstoque   = useLatch(monitor.estoque?.startOP,   !monitor.estoque?.pedidoEmCurso);
  const finalizadoProcesso  = useLatch(monitor.processo?.startOP,  !monitor.estoque?.pedidoEmCurso);
  const finalizadoMontagem  = useLatch(monitor.montagem?.startOP,  !monitor.estoque?.pedidoEmCurso);
  const finalizadoExpedicao = useLatch(monitor.expedicao?.startOP, !monitor.estoque?.pedidoEmCurso);

  // Map para os status das estações que são manipulados pelo latch
  const statusEstacoes: Record<Estacao, EstacaoStatusModule> = {
    [Estacao.Estoque]: EstacaoStatusModule.Desligado,
    [Estacao.Processo]: EstacaoStatusModule.Desligado,
    [Estacao.Montagem]: EstacaoStatusModule.Desligado,
    [Estacao.Expedicao]: EstacaoStatusModule.Desligado
  }

  const statusPipelines: Record<Estacao, EstacaoStatusPipe> = {
    [Estacao.Estoque]: EstacaoStatusPipe.Desligado,
    [Estacao.Processo]: EstacaoStatusPipe.Desligado,
    [Estacao.Montagem]: EstacaoStatusPipe.Desligado,
    [Estacao.Expedicao]: EstacaoStatusPipe.Desligado
  }


  return {
    estoque,
    expedicao,
    monitor,
    statusEstacoes,
    statusPipelines,
    bancada,
    erro: estoque.erro ?? expedicao.erro,
    dismissErro,
  };
}
