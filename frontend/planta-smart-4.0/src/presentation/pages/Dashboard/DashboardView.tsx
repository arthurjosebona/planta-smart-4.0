import { useDashboardViewModel } from '@pages/Dashboard/useDashboardViewModel';
import { EstoqueSection } from '@components/organisms/EstoqueSection/EstoqueSection';
import { ExpedicaoSection } from '@components/organisms/ExpedicaoSection/ExpedicaoSection';
import { FeedbackBanner } from '@components/atoms/FeedbackBanner/FeedbackBanner';
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
    dismissErro,
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
      <main id="main-content" className={styles.main}>
        {model.erro && (
          <FeedbackBanner
            variant="error"
            message={model.erro}
            onDismiss={dismissErro}
          />
        )}

        <div className={styles.sections}>
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