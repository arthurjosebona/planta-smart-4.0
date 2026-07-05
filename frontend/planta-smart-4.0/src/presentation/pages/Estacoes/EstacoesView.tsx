import styles from './EstacoesView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';
import { Estacoes } from '@components/organisms/EstacoesSection/Estacoes';
import { FeedbackBanner } from '@components/atoms/FeedbackBanner/FeedbackBanner';
import { ViewEstoque } from '@components/molecules/ViewEstoque/ViewEstoque';
import { ViewExpedicao } from '@components/molecules/ViewExpedicao/ViewExpedicao';
import { OpEmCursoCard } from '@components/molecules/OpEmCursoCard/OpEmCursoCard';
import { useEstacoesViewModel } from './useEstacoesViewModel';
import { Estacao } from '@enums/Estacao';

export default function EstacoesView() {
  const { estoque, expedicao, monitor, statusEstacoes, statusPipelines, statusEsteiras, bancada, erro, dismissErro, pedidoAtual, tempoDecorrido } = useEstacoesViewModel();

  const numeroOP = monitor.estoque?.numeroOP ?? 0;
  const statusProducao = monitor.estoque?.statusProducao ?? 0;


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
            <Estacoes status={monitor} statusEstacoes={statusEstacoes} statusPipelines={statusPipelines} />
            <div className={styles.esteiraRow}>
              {([Estacao.Estoque, Estacao.Processo, Estacao.Montagem, Estacao.Expedicao] as Estacao[]).map((estacao) => (
                <div key={estacao} className={styles.esteiraCard}>
                  <span className={styles.esteiraLabel}>{estacao}</span>
                  <span className={styles.esteiraStatus}>{statusEsteiras[estacao] || '—'}</span>
                </div>
              ))}
            </div>
          </section>

          <OpEmCursoCard pedido={pedidoAtual} pedidoEmCurso={!!monitor.estoque?.pedidoEmCurso} tempoDecorrido={tempoDecorrido} />

          <div className={styles.inferiorEstoques}>
            <section className={styles.painel}>
              <span className={styles.painelLabel}>Estoque</span>
              <ViewEstoque
                estoque={bancada.estoque}
                editMode={false}
                selectedIds={[]}
                onToggle={() => {}}
              />
            </section>

            <section className={styles.painel}>
              <span className={styles.painelLabel}>Expedição</span>
              <ViewExpedicao expedicao={bancada.expedicao} editMode={false} />
            </section>
          </div>
        </div>
      </main>
    </AppTemplate>
  );
}
