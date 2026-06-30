import { useState, useEffect } from 'react';
import { PedidosModel, PedidosModelInitial } from '@pages/Pedidos/PedidosModel';
import { pedidoService } from '@config/diContainer';
import { HttpError } from '@error/HttpError';
import { Pedido } from '@entities/Pedido';
import { StatusPedido } from '@enums/StatusPedido';
import { useStatusContext } from '@contexts/StatusContext';

export function usePedidosViewModel() {
  const [model, setModel] = useState<PedidosModel>(PedidosModelInitial);
  const { conectado } = useStatusContext();

  useEffect(() => {
    fetchPedidos();
  }, []);

  async function fetchPedidos() {
    setModel((s) => ({ ...s, loading: true, erro: null }));
    try {
      const pedidos = await pedidoService.findAll();
      setModel((s) => ({ ...s, pedidos, loading: false }));
    } catch (error: unknown) {
      const mensagem = error instanceof HttpError ? error.message : 'Erro ao carregar pedidos.';
      setModel((s) => ({ ...s, loading: false, erro: mensagem }));
    }
  }

  async function iniciarProducao(id: number) {
    setModel((s) => ({ ...s, loading: true, erro: null }));
    try {
      console.log('Iniciando produção: ' + id);
      const atualizado: Pedido = await pedidoService.iniciarProducao(id);
      setModel((s) => ({
        ...s,
        loading: false,
        erro: null,
        pedidos: s.pedidos.map((p) => (p.id === id ? atualizado : p)),
      }));
    } catch (error: unknown) {
      const mensagem = error instanceof HttpError ? error.message : 'Erro ao iniciar produção.';
      setModel((s) => ({ ...s, loading: false, erro: mensagem }));
    }
  }

  async function deletarPedido(id: number) {
    setModel((s) => ({ ...s, loading: true, erro: null }));
    try {
      await pedidoService.delete(id);
      setModel((s) => ({
        ...s,
        loading: false,
        erro: null,
        pedidos: s.pedidos.filter((p) => p.id !== id),
      }));
    } catch (error: unknown) {
      const mensagem = error instanceof HttpError ? error.message : 'Erro ao deletar pedido.';
      setModel((s) => ({ ...s, loading: false, erro: mensagem }));
    }
  }

  async function enviarParaProducao(id: number) {
    setModel((s) => ({ ...s, loading: true, erro: null }));
    try {
      await pedidoService.enviarParaProducao(id);
      // A fila é atualizada via SSE; recarrega a lista para refletir a mudança
      // de status do pedido (PENDENTE -> PRODUCAO) nos cards.
      await fetchPedidos();
    } catch (error: unknown) {
      const mensagem =
        error instanceof HttpError ? error.message : 'Erro ao enviar pedido para produção.';
      setModel((s) => ({ ...s, loading: false, erro: mensagem }));
    }
  }

  function dismissErro() {
    setModel((s) => ({ ...s, erro: null }));
  }

  function setStatusPedidoFiltro(tipo: StatusPedido | null) {
    setModel((s) => ({
      ...s,
      filtroStatus: s.filtroStatus === tipo ? null : tipo,
    }));
  }

  const pedidosFiltrados =
    model.filtroStatus === null
      ? model.pedidos
      : model.pedidos.filter((p) => p.status === model.filtroStatus);

  return {
    model,
    conectado,
    iniciarProducao,
    enviarParaProducao,
    deletarPedido,
    dismissErro,
    pedidosFiltrados,
    setStatusPedidoFiltro,
  };
}