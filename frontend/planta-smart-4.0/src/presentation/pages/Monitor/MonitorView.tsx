import { useMonitorViewModel } from './useMonitorViewModel';
import { AppTemplate } from '@components/template/AppTemplate';
import { ClpMonitorGrid } from '@components/organisms/ClpMonitorGrid/ClpMonitorGrid';
import styles from './monitorView.module.css';

export default function MonitorView() {
  const model = useMonitorViewModel();

  return (
    <AppTemplate>
      <main id="main-content" className={styles.main}>
        <div className={styles.titleRow}>
          <h1 className={styles.title}>Monitor CLP</h1>
          <div className={`${styles.streamStatus} ${model.conectado ? styles.online : styles.offline}`}>
            <span className={styles.streamDot} aria-hidden="true" />
            <span>{model.conectado ? 'Stream ativo' : 'Sem conexão'}</span>
          </div>
        </div>

        <ClpMonitorGrid model={model} />
      </main>
    </AppTemplate>
  );
}
