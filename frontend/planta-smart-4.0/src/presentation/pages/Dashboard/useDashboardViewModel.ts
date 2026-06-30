import { useState } from 'react';
import { useEstoqueContext } from '@contexts/EstoqueContext';
import { useExpedicaoContext } from '@contexts/ExpedicaoContext';
import { StoreModel } from '@pages/Dashboard/DashboardModel';
import { Expedicao } from '@entities/Expedicao';
import { pedidoService } from '@config/diContainer';

export function useDashboardViewModel() {
  const estoque = useEstoqueContext();
  const expedicao = useExpedicaoContext();

  // Slot de expedição aberto no modal de detalhe (clique fora do modo de edição).
  const [detalheSlot, setDetalheSlot] = useState<Expedicao | null>(null);

  function abrirDetalheExpedicao(id: number) {
    const slot = expedicao.expedicao.find((s) => s.id === id) ?? null;
    setDetalheSlot(slot);
  }

  function fecharDetalheExpedicao() {
    setDetalheSlot(null);
  }

  async function iniciarProducao(id: number) {
    await pedidoService.iniciarProducao(id);
  }

  const model: StoreModel = {
    estoque: estoque.estoque,
    expedicao: expedicao.expedicao,
    expedicaoSnapshot: [],
    editMode: estoque.editMode,
    selectedIds: estoque.selectedIds,
    loading: estoque.loading || expedicao.loading,
    erro: estoque.erro ?? expedicao.erro,
    expedicaoEditMode: expedicao.editMode,
    selectedExpedicaoId: expedicao.selectedId,
    opInput: expedicao.opInput,
  };

  function dismissErro() {
    estoque.dismissErro();
    expedicao.dismissErro();
  }

  return {
    model,
    // detalhe expedição
    detalheSlot,
    abrirDetalheExpedicao,
    fecharDetalheExpedicao,
    iniciarProducao,
    // estoque
    enterEditMode: estoque.enterEditMode,
    cancelEditMode: estoque.cancelEditMode,
    toggleBlocoSelection: estoque.toggleBlocoSelection,
    changeBlockColor: estoque.changeBlockColor,
    cleanEstoque: estoque.cleanEstoque,
    saveEstoque: estoque.saveEstoque,
    dismissErro,
    // expedição
    enterExpedicaoEditMode: expedicao.enterEditMode,
    cancelExpedicaoEditMode: expedicao.cancelEditMode,
    selectSlot: expedicao.selectSlot,
    changeOpInput: expedicao.changeOpInput,
    saveExpedicao: expedicao.saveExpedicao,
  };
}
