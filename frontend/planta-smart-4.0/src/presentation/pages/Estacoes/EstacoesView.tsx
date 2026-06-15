import React, { useState } from 'react';
import styles from './EstacoesView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';
import {
  Estacoes,
  BancadaStatus,
  BANCADA_STATUS_DEFAULT,
} from '@components/organisms/EstacoesSection/Estacoes';

/**
 * MonitorView
 *
 * Exibe a bancada Smart 4.0 centralizada na tela.
 * O estado de cada módulo (estoque, processo, montagem, expedição)
 * é controlado externamente — futuramente via CLP (WebSocket / MQTT / polling).
 *
 * Para integrar com o CLP substitua o `useState` local por um hook
 * que leia os dados em tempo real, ex:
 *
 *   const status = usePlcStatus();   // hook que retorna BancadaStatus
 */
export default function MonitorView() {
  // Placeholder: status inicial. Será substituído pelo hook do CLP.
  const [status] = useState<BancadaStatus>(BANCADA_STATUS_DEFAULT);

  return (
    <AppTemplate>
      <main id="main-content" className={styles.main}>
        <Estacoes status={status} />
      </main>
    </AppTemplate>
  );
}
