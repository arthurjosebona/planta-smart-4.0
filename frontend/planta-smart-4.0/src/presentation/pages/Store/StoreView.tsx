import { OrderForm } from '@components/organisms/OrderForm';
import { OrderViewer } from '@components/organisms/OrderViewer';
import { useStoreViewModel } from './useStoreViewModel';

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
    <div
      style={{
        width: '100vw',
        height: '100vh',
        padding: 20,
        boxSizing: 'border-box',
        background: '#f5f5f5',
      }}
    >
      <h1>Configurador 3D de Blocos</h1>

      <div
        style={{
          display: 'flex',
          flexDirection: 'row',
          width: '100%',
          height: 580,
          overflow: 'hidden',
          borderRadius: 8,
          boxShadow: '0 2px 12px rgba(0,0,0,0.10)',
        }}
      >
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
  );
}
