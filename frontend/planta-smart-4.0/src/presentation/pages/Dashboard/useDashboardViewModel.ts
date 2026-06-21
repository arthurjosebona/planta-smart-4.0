import { useEstoqueContext } from '@contexts/EstoqueContext';
import { useExpedicaoContext } from '@contexts/ExpedicaoContext';
import { StoreModel } from '@pages/Dashboard/DashboardModel';

export function useDashboardViewModel() {
  const estoque = useEstoqueContext();
  const expedicao = useExpedicaoContext();

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
