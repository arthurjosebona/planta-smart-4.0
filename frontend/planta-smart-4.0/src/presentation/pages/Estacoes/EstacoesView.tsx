import { useState } from 'react';
import styles from './EstacoesView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';
import {
  Estacoes,
  BancadaStatus,
  BANCADA_STATUS_DEFAULT,
} from '@components/organisms/EstacoesSection/Estacoes';
import { EstoqueSection } from '@components/organisms/EstoqueSection/EstoqueSection';
import { ExpedicaoSection } from '@components/organisms/ExpedicaoSection/ExpedicaoSection';
import { FeedbackBanner } from '@components/atoms/FeedbackBanner/FeedbackBanner';
import { useEstacoesViewModel } from './useEstacoesViewModel';
import { ViewEstoque } from '@components/molecules/ViewEstoque/ViewEstoque';
import { ViewExpedicao } from '@components/molecules/ViewExpedicao/ViewExpedicao';

/**
 * EstacoesView
 *
 * Exibe a bancada Smart 4.0 ao centro, com o Estoque à esquerda e a
 * Expedição à direita. O estado de cada módulo (estoque, processo, montagem,
 * expedição) é controlado externamente — futuramente via CLP.
 *
 * Estoque e Expedição compartilham estado via Context API
 * (EstoqueProvider / ExpedicaoProvider) com a tela de Dashboard.
 */
export default function EstacoesView() {
  // Placeholder: status inicial. Será substituído pelo hook do CLP.
  const [status] = useState<BancadaStatus>(BANCADA_STATUS_DEFAULT);
  const { estoque, expedicao, erro, dismissErro } = useEstacoesViewModel();

  return (
    <AppTemplate>
      <main id="main-content" className={styles.main}>
        {erro && <FeedbackBanner variant="error" message={erro} onDismiss={dismissErro} />}

        <div className={styles.layout}>
          <Estacoes status={status} />

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
