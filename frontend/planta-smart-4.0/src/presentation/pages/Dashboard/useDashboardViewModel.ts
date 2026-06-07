import { useState, useEffect } from 'react';
import { StoreModel, StoreModelInitial } from '@pages/Dashboard/DashboardModel';
import { CorEstoque } from '@enums/CorEstoque';
import { estoqueService, expedicaoService } from '@config/diContainer';
import { HttpError } from '@error/HttpError';

export function useDashboardViewModel() {
  const [model, setModel] = useState<StoreModel>(StoreModelInitial);

  useEffect(() => {
    fetchAll();
  }, []);

  async function fetchAll() {
    setModel((s) => ({ ...s, loading: true, erro: null }));
    try {
      const [estoque, expedicao] = await Promise.all([
        estoqueService.findAll(),
        expedicaoService.findAll(),
      ]);
      setModel((s) => ({ ...s, estoque, expedicao, loading: false }));
    } catch {
      setModel((s) => ({
        ...s,
        loading: false,
        erro: 'Erro ao carregar dados.',
      }));
    }
  }

  // ── Edição de estoque ──────────────────────────────────────────────────────
  function enterEditMode() {
    setModel((s) => ({ ...s, editMode: true, selectedIds: [] }));
  }

  function cancelEditMode() {
    setModel((s) => ({ ...s, editMode: false, selectedIds: [] }));
  }

  function toggleBlocoSelection(id: number) {
    setModel((s) => {
      const already = s.selectedIds.includes(id);
      return {
        ...s,
        selectedIds: already ? s.selectedIds.filter((i) => i !== id) : [...s.selectedIds, id],
      };
    });
  }

  function changeBlockColor(cor: CorEstoque) {
    setModel((s) => ({
      ...s,
      estoque: s.estoque.map((bloco) =>
        s.selectedIds.includes(bloco.id) ? { ...bloco, cor: cor } : bloco
      ),
      // Desseleciona após aplicar a cor
      selectedIds: [],
    }));
  }

  function cleanEstoque() {
    setModel((s) => ({
      ...s,
      estoque: s.estoque.map((bloco) =>
        s.selectedIds.includes(bloco.id) ? { ...bloco, cor: CorEstoque.Vazio } : bloco
      ),
      selectedIds: [],
    }));
  }

  async function saveEstoque() {
    setModel((s) => ({ ...s, loading: true, erro: null }));
    try {
      await estoqueService.updateAll(model.estoque);
      setModel((s) => ({ ...s, loading: false, editMode: false }));
    } catch (error: unknown) {
      const mensagem = error instanceof HttpError ? error.message : 'Erro desconhecido';
      setModel((s) => ({
        ...s,
        loading: false,
        erro: mensagem,
      }));
    }
  }

  return {
    model,
    enterEditMode,
    cancelEditMode,
    toggleBlocoSelection,
    changeBlockColor,
    cleanEstoque,
    saveEstoque,
  };
}
