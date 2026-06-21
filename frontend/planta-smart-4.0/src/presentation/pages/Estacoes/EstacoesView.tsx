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
        <header className={styles.header}>
          <h1 className={styles.heading}>Estações</h1>
          <p className={styles.subheading}>
            Monitoramento em tempo real da linha de produção
          </p>
        </header>

        {erro && <FeedbackBanner variant="error" message={erro} onDismiss={dismissErro} />}

        <div className={styles.layout}>
          <section className={styles.estacoesSection}>
            <Estacoes status={monitor} moduleStatus={moduleStatus} />
          </section>

          <OpEmCursoCard numeroOP={numeroOP} pedidoEmCurso={pedidoEmCurso} />

          <div className={styles.inferiorEstoques}>
            <section className={styles.painel}>
              <span className={styles.painelLabel}>Estoque</span>
              <ViewEstoque
                estoque={estoque.estoque}
                editMode={estoque.editMode}
                selectedIds={estoque.selectedIds}
                onToggle={estoque.toggleBlocoSelection}
              />
            </section>

            <section className={styles.painel}>
              <span className={styles.painelLabel}>Expedição</span>
              <ViewExpedicao
                expedicao={expedicao.expedicao}
                editMode={expedicao.editMode}
                selectedId={expedicao.selectedId}
                onToggle={expedicao.selectSlot}
              />
            </section>
          </div>
        </div>
      </main>
    </AppTemplate>
  );
}
