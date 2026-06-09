import React from 'react';
import { usePedidosViewModel } from '@pages/Pedidos/usePedidosViewModel';
import { PedidosSection } from '@components/organisms/PedidoSection/PedidosSection';
import styles from './pedidosView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';

export default function PedidosView() {
  const { model, pedidosFiltrados, iniciarProducao, setStatusPedidoFiltro } = usePedidosViewModel();

  return (
    <AppTemplate>
      <main id="main-content" className={styles.main}>
        {model.erro && (
          <div className="erro-banner" role="alert">
            {model.erro}
          </div>
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