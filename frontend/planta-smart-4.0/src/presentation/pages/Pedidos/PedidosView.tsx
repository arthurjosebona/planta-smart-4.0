import React from 'react';
import { usePedidosViewModel } from '@pages/Pedidos/usePedidosViewModel';
import { PedidosSection } from '@components/organisms/PedidoSection/PedidosSection';
import styles from './pedidosView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';

export default function PedidosView() {
  const { model, iniciarProducao } = usePedidosViewModel();

  return (
    <AppTemplate>
      <main id="main-content" className={styles.main}>
        {model.erro && (
          <div className="erro-banner" role="alert">
            {model.erro}
          </div>
        )}
        <PedidosSection
          pedidos={model.pedidos}
          loading={model.loading}
          iniciarProducao={iniciarProducao}
        />
      </main>
    </AppTemplate>
  );
}
