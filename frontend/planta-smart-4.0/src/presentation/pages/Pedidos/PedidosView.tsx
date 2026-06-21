import React from 'react';
import { usePedidosViewModel } from '@pages/Pedidos/usePedidosViewModel';
import { PedidosSection } from '@components/organisms/PedidoSection/PedidosSection';
import { FeedbackBanner } from '@components/atoms/FeedbackBanner/FeedbackBanner';
import styles from './pedidosView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';

export default function PedidosView() {
  const { model, pedidosFiltrados, iniciarProducao, dismissErro, setStatusPedidoFiltro } =
    usePedidosViewModel();

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
        <PedidosSection
          pedidos={pedidosFiltrados}
          loading={model.loading}
          filtroStatus={model.filtroStatus}
          onFiltroStatus={setStatusPedidoFiltro}
          iniciarProducao={iniciarProducao}
        />
      </main>
    </AppTemplate>
  );
}