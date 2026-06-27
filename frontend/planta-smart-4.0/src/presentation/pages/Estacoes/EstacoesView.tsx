import styles from './EstacoesView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';
import { Estacoes } from '@components/organisms/EstacoesSection/Estacoes';
import { FeedbackBanner } from '@components/atoms/FeedbackBanner/FeedbackBanner';
import { ViewEstoque } from '@components/molecules/ViewEstoque/ViewEstoque';
import { ViewExpedicao } from '@components/molecules/ViewExpedicao/ViewExpedicao';
import { OpEmCursoCard } from '@components/molecules/OpEmCursoCard/OpEmCursoCard';
import { useEstacoesViewModel } from './useEstacoesViewModel';

export default function EstacoesView() {
  const { estoque, expedicao, monitor, statusEstacoes, statusPipelines, bancada, erro, dismissErro } = useEstacoesViewModel();

  const numeroOP = monitor.estoque?.numeroOP ?? 0;
  const statusProducao = monitor.estoque?.statusProducao ?? 0;

  // "Pedido em curso" = há uma OP carregada na bancada que ainda não foi concluída.
  // Não usamos a flag pedidoEmCurso do CLP porque o backend só a seta para true
  // (em EstoqueComm.confirmarInicioPedido) e nunca a reseta. Em vez disso, derivamos
  // pela OP corrente (numeroOP > 0) enquanto a produção não foi finalizada na expedição
  // (statusProducao vira 1 ao concluir e volta a 0 no início da próxima ordem).
  const pedidoEmCurso = numeroOP > 0 && statusProducao !== 1;

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
          </section>

          <OpEmCursoCard numeroOP={numeroOP} pedidoEmCurso={pedidoEmCurso} />

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
