import { OrderForm } from '@components/organisms/OrderForm/OrderForm';
import { OrderViewer } from '@components/organisms/OrderViewer/OrderViewer';
import { useStoreViewModel } from './useStoreViewModel';
import styles from './StoreView.module.css';
import { AppTemplate } from '@components/template/AppTemplate';

export default function StoreView() {
  const {
    model,
    setNumBlocos,
    setCorTampa,
    setBlocoField,
    setBlocoColor,
    setLaminaCor,
    setLaminaPadrao,
    setOrdemDeProducao,
    createPedido,
  } = useStoreViewModel();

  return (
    <AppTemplate>
      <div className={styles.page}>
        <h1 className={styles.heading}>Configurador 3D de Blocos</h1>

        <div className={styles.configurator}>
          <OrderForm
            state={model}
            setNumBlocos={setNumBlocos}
            setCorTampa={setCorTampa}
            setBlocoField={setBlocoField}
            setBlocoColor={setBlocoColor}
            setLaminaCor={setLaminaCor}
            setLaminaPadrao={setLaminaPadrao}
            setOrdemDeProducao={setOrdemDeProducao}
            createPedido={createPedido}
          />
          <OrderViewer state={model} />
        </div>
      </div>
    </AppTemplate>
  );
}
