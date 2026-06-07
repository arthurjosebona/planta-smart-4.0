import { useDashboardViewModel } from '@pages/Dashboard/useDashboardViewModel';
import { EstoqueSection } from '@components/organisms/EstoqueSection/EstoqueSection';
import { ExpedicaoSection } from '@components/organisms/ExpedicaoSection/ExpedicaoSection';
import styles from './dashboardView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';

export default function DashboardView() {
  const {
    model,
    enterEditMode,
    cancelEditMode,
    toggleBlocoSelection,
    changeBlockColor,
    cleanEstoque,
    saveEstoque,
  } = useDashboardViewModel();

  if (model.loading && model.estoque.length === 0) {
    return (
      <main id="main-content">
        <p className={styles['loading-msg']}>Carregando…</p>
      </main>
    );
  }

  return (
    <AppTemplate>
      <main id="main-content">
        {model.erro && (
          <div className="erro-banner" role="alert">
            {model.erro}
          </div>
        )}
        <EstoqueSection
          estoque={model.estoque}
          editMode={model.editMode}
          selectedIds={model.selectedIds}
          loading={model.loading}
          onEnterEdit={enterEditMode}
          onCancel={cancelEditMode}
          onToggleBloco={toggleBlocoSelection}
          onChangeColor={changeBlockColor}
          onClean={cleanEstoque}
          onSave={saveEstoque}
        />
        <ExpedicaoSection expedicao={model.expedicao} />
      </main>
    </AppTemplate>
  );
}
