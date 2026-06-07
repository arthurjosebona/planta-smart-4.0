import { useState, useEffect } from 'react';
import { PedidosModel, PedidosModelInitial } from '@pages/Pedidos/PedidosModel';
import { pedidoService } from '@config/diContainer';
import { HttpError } from '@error/HttpError';

export function usePedidosViewModel() {
  const [model, setModel] = useState<PedidosModel>(PedidosModelInitial);

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

  async function iniciarProducao() {
    setModel((s) => ({ ...s, loading: true, erro: null }));
    try {
      // await pedidoService.iniciarProducao();
      setModel((s) => ({ ...s, loading: false, erro: null }));
    } catch (error: unknown) {
      const mensagem = error instanceof HttpError ? error.message : 'Erro ao carregar pedidos.';
      setModel((s) => ({ ...s, loading: false, erro: mensagem }));
    }
  }

  return {
    model,
    iniciarProducao,
  };
}
