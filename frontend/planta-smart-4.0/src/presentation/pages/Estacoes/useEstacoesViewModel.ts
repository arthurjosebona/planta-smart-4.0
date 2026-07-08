import { useEstoqueContext } from '@contexts/EstoqueContext';
import { useExpedicaoContext } from '@contexts/ExpedicaoContext';
import { useMonitorContext } from '@contexts/MonitorContext';
import { usePingContext } from '@contexts/PingContext';
import { useStatusContext } from '@contexts/StatusContext';
import { StatusEstacao } from '@enums/StatusEstacao';
import { CorEstoque } from '@enums/CorEstoque';
import type { Estoque } from '@entities/Estoque';
import type { Expedicao } from '@entities/Expedicao';
import { useEffect, useRef, useState } from 'react';
import { Estacao } from '@enums/Estacao';
import { EstacaoStatusModule, IntToEstacaoStatusModule } from '@enums/EstacaoStatusModule';
import { EstacaoStatusPipe } from '@enums/EstacaoStatusPipe';
import { Pedido } from '@entities/Pedido';
import { pedidoService } from '@config/diContainer';
import { useTempoDecorrido } from '@hooks/useTempoDecorrido';
import { MonitorModel } from '@pages/Monitor/MonitorModel';

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

// function computeOverlayStatus(
//   ligado: boolean,
//   pedidoEmCurso: boolean,
//   ocupado: boolean,
//   finalizado: boolean
// ): EstacaoStatusModule {
//   if (!ligado) return EstacaoStatusModule.Desligado;
//   if (!pedidoEmCurso) return EstacaoStatusModule.Aguardando;
//   if (ocupado) return EstacaoStatusModule.Ocupado;
//   if (finalizado) return EstacaoStatusModule.Finalizado;
//   return EstacaoStatusModule.Aguardando; // fallback enquanto não ocupou nem finalizou
// }

function computePipelineStatus(online: boolean, pausado: boolean): EstacaoStatusPipe {
  if (online) return EstacaoStatusPipe.Ligado;
  if (pausado) return EstacaoStatusPipe.Ocupado;
  return EstacaoStatusPipe.Desligado;
}

export function useEstacoesViewModel() {
  const estoque = useEstoqueContext();
  const expedicao = useExpedicaoContext();
  const monitor: MonitorModel = useMonitorContext();
  const { pingMap } = usePingContext();
  const { conectado } = useStatusContext();
  const hasEverConnected = useRef(false);
  const [pedidoAtual, setPedidoAtual] = useState<Pedido | null>(null);
  const ultimaOpBuscada = useRef<number | null>(null);

  if (conectado) hasEverConnected.current = true;

  useEffect(() => {
    if (!monitor.estoque?.numeroOP || monitor.estoque?.numeroOP == 0 || monitor.estoque?.numeroOP === ultimaOpBuscada.current) return;

    ultimaOpBuscada.current = monitor.estoque?.numeroOP;

    pedidoService.findByOrdemDeProducao(monitor.estoque?.numeroOP)
      .then(setPedidoAtual)
  }, [monitor.estoque?.numeroOP]);

  const tempoDecorrido = useTempoDecorrido(pedidoAtual?.registroEntradaEstoque);

  function dismissErro() {
    estoque.dismissErro();
    expedicao.dismissErro();
  }

  // Conteúdo físico das magazines da bancada, derivado do stream SSE (MonitorContext).
  const bancada = {
    estoque: mapBancadaEstoque(monitor.estoque?.posicoesOcupadas),
    expedicao: mapBancadaExpedicao(monitor.expedicao?.orderExpedicao),
  };

  // const finalizadoEstoque   = useLatch(monitor.estoque?.startOP,   !monitor.estoque?.pedidoEmCurso);
  // const finalizadoProcesso  = useLatch(monitor.processo?.startOP,  !monitor.estoque?.pedidoEmCurso);
  // const finalizadoMontagem  = useLatch(monitor.montagem?.startOP,  !monitor.estoque?.pedidoEmCurso);
  // const finalizadoExpedicao = useLatch(monitor.expedicao?.startOP, !monitor.estoque?.pedidoEmCurso);

  // // Map para os status das estações que são manipulados pelo latch
  // const statusEstacoes: Record<Estacao, EstacaoStatusModule> = {
  //   [Estacao.Estoque]: computeOverlayStatus(
  //     !!pingMap[Estacao.Estoque],
  //     !!monitor.estoque?.pedidoEmCurso,
  //     !!monitor.estoque?.ocupado,
  //     finalizadoEstoque
  //   ),
  //   [Estacao.Processo]: computeOverlayStatus(
  //     !!pingMap[Estacao.Processo],
  //     !!monitor.estoque?.pedidoEmCurso,
  //     !!monitor.processo?.ocupado,
  //     finalizadoProcesso
  //   ),
  //   [Estacao.Montagem]: computeOverlayStatus(
  //     !!pingMap[Estacao.Montagem],
  //     !!monitor.estoque?.pedidoEmCurso,
  //     !!monitor.montagem?.ocupado,
  //     finalizadoMontagem
  //   ),
  //   [Estacao.Expedicao]: computeOverlayStatus(
  //     !!pingMap[Estacao.Processo],
  //     !!monitor.estoque?.pedidoEmCurso,
  //     !!monitor.expedicao?.ocupado,
  //     finalizadoExpedicao
  //   ),
  // }

  const statusEstacoes: Record<Estacao, EstacaoStatusModule> = conectado ? {
    [Estacao.Estoque]: IntToEstacaoStatusModule[monitor.estoque?.statusEstoque ?? 0] ?? EstacaoStatusModule.Aguardando,
    [Estacao.Processo]: IntToEstacaoStatusModule[monitor.estoque?.statusProcesso ?? 0] ?? EstacaoStatusModule.Aguardando,
    [Estacao.Montagem]: IntToEstacaoStatusModule[monitor.estoque?.statusMontagem ?? 0] ?? EstacaoStatusModule.Aguardando,
    [Estacao.Expedicao]: IntToEstacaoStatusModule[monitor.estoque?.statusExpedicao ?? 0] ?? EstacaoStatusModule.Aguardando,
  } : {
    [Estacao.Estoque]: EstacaoStatusModule.Desligado,
    [Estacao.Processo]: EstacaoStatusModule.Desligado,
    [Estacao.Montagem]: EstacaoStatusModule.Desligado,
    [Estacao.Expedicao]: EstacaoStatusModule.Desligado,
  };

  const pausado = !conectado && hasEverConnected.current;

  const statusPipelines: Record<Estacao, EstacaoStatusPipe> = {
    [Estacao.Estoque]: computePipelineStatus(!!pingMap.estoque, pausado),
    [Estacao.Processo]: computePipelineStatus(!!pingMap.processo, pausado),
    [Estacao.Montagem]: computePipelineStatus(!!pingMap.montagem, pausado),
    [Estacao.Expedicao]: computePipelineStatus(!!pingMap.expedicao, pausado),
  };


  return {
    estoque,
    expedicao,
    monitor,
    statusEstacoes,
    statusPipelines,
    bancada,
    erro: estoque.erro ?? expedicao.erro,
    dismissErro,
    pedidoAtual,
    tempoDecorrido,
  };
}
