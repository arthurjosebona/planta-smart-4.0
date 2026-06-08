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
      <AppTemplate>
        <main id="main-content">
          <p className={styles['loading-msg']}>Carregando…</p>
        </main>
      </AppTemplate>
    );
  }

  return (
    <AppTemplate>
      <main id="main-content" className={styles.main}> {/* ← estava sem className */}
        {model.erro && (
          <div className="erro-banner" role="alert">
            {model.erro}
          </div>
        )}

        <div className={styles.sections}> {/* ← wrapper novo */}
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
        </div>
      </main>
    </AppTemplate>
  );
}
