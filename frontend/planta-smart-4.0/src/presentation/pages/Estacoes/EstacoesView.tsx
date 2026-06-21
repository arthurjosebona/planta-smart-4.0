import styles from './EstacoesView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';
import { Estacoes } from '@components/organisms/EstacoesSection/Estacoes';
import { FeedbackBanner } from '@components/atoms/FeedbackBanner/FeedbackBanner';
import { ViewEstoque } from '@components/molecules/ViewEstoque/ViewEstoque';
import { ViewExpedicao } from '@components/molecules/ViewExpedicao/ViewExpedicao';
import { OpEmCursoCard } from '@components/molecules/OpEmCursoCard/OpEmCursoCard';
import { useEstacoesViewModel } from './useEstacoesViewModel';

export default function EstacoesView() {
  const { estoque, expedicao, monitor, moduleStatus, erro, dismissErro } = useEstacoesViewModel();

  const numeroOP = monitor.estoque?.numeroOP ?? 0;
  const pedidoEmCurso = monitor.estoque?.pedidoEmCurso ?? false;

  return (
    <AppTemplate>
      <main id="main-content" className={styles.main}>
        {erro && <FeedbackBanner variant="error" message={erro} onDismiss={dismissErro} />}

        <div className={styles.layout}>
          <Estacoes status={monitor} moduleStatus={moduleStatus} />

          <OpEmCursoCard numeroOP={numeroOP} pedidoEmCurso={pedidoEmCurso} />

          <div className={styles.inferiorEstoques}>
            <ViewEstoque
              estoque={estoque.estoque}
              editMode={estoque.editMode}
              selectedIds={estoque.selectedIds}
              onToggle={estoque.toggleBlocoSelection}
            />

            <ViewExpedicao
              expedicao={expedicao.expedicao}
              editMode={expedicao.editMode}
              selectedId={expedicao.selectedId}
              onToggle={expedicao.selectSlot}
            />
          </div>
        </div>
      </main>
    </AppTemplate>
  );
}
