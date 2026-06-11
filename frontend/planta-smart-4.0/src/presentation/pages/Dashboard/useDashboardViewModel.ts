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

  function dismissErro() {
    setModel((s) => ({ ...s, erro: null }));
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

  function enterExpedicaoEditMode() {
    setModel((s) => ({
      ...s,
      expedicaoEditMode: true,
      expedicaoSnapshot: s.expedicao,
      selectedExpedicaoId: null,
      opInput: '',
    }));
  }

  function cancelExpedicaoEditMode() {
    setModel((s) => ({
      ...s,
      expedicaoEditMode: false,
      expedicao: s.expedicaoSnapshot,
      expedicaoSnapshot: [],
      selectedExpedicaoId: null,
      opInput: '',
    }));
  }

  function selectSlot(id: number) {
    setModel((s) => {
      // aplica o input pendente no slot anterior antes de trocar
      const expedicaoAtualizada =
        s.selectedExpedicaoId !== null
          ? s.expedicao.map((slot) =>
              slot.id === s.selectedExpedicaoId
                ? { ...slot, ordemDeProducaoAtual: Number(s.opInput) }
                : slot
            )
          : s.expedicao;

      const slotSelecionado = expedicaoAtualizada.find((slot) => slot.id === id);

      return {
        ...s,
        expedicao: expedicaoAtualizada,
        selectedExpedicaoId: id,
        opInput: slotSelecionado?.ordemDeProducaoAtual?.toString() ?? '',
      };
    });
  }

  function changeOpInput(value: string) {
    setModel((s) => ({ ...s, opInput: value }));
  }

  async function saveExpedicao() {
    // aplica o input pendente do slot ainda aberto antes de salvar
    const expedicaoFinal =
      model.selectedExpedicaoId !== null
        ? model.expedicao.map((slot) =>
            slot.id === model.selectedExpedicaoId
              ? { ...slot, ordemDeProducaoAtual: Number(model.opInput) }
              : slot
          )
        : model.expedicao;

    setModel((s) => ({ ...s, loading: true, erro: null, expedicao: expedicaoFinal }));
    try {
      expedicaoService.updateAll(model.expedicao);
      setModel((s) => ({
        ...s,
        loading: false,
        expedicaoEditMode: false,
        expedicaoSnapshot: [],
        selectedExpedicaoId: null,
        opInput: '',
      }));
    } catch (error: unknown) {
      const mensagem = error instanceof HttpError ? error.message : 'Erro desconhecido';
      setModel((s) => ({ ...s, loading: false, erro: mensagem }));
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
    dismissErro,
    enterExpedicaoEditMode,
  cancelExpedicaoEditMode,
  selectSlot,
  changeOpInput,
  saveExpedicao,
  };
}