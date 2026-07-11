import { ClpValueField } from '@components/atoms/ClpValueField/ClpValueField';
import { EstoqueStream } from '@entities/stream/EstoqueStream';
import styles from './infoPedidoCard.module.css';

interface InfoPedidoCardProps {
  data: EstoqueStream | null;
}

const ESTOQUE_COLOR = '#378ADD';

export function InfoPedidoCard({ data }: InfoPedidoCardProps) {
  return (
    <div className={styles.card} style={{ '--station-color': ESTOQUE_COLOR } as React.CSSProperties}>
      <div className={styles.accent} />

      <div className={styles.header}>
        <span className={styles.name}>Info Pedido</span>
        {!data && <span className={styles.waiting}>Aguardando stream…</span>}
      </div>

      {data ? (
        <div className={styles.body}>
          <div className={styles.resumo}>
            <ClpValueField label="numeroPedido" value={data.numeroPedido} />
            <ClpValueField label="andares" value={data.andares} />
            <ClpValueField label="posicaoExpedicao" value={data.posicaoExpedicao} />
          </div>

          <div className={styles.andares}>
            <AndarSection label="Andar 1" data={{
              cor: data.corAndar1,
              posicaoEstoque: data.posicaoEstoqueAndar1,
              corLamina1: data.corLamina1Andar1,
              corLamina2: data.corLamina2Andar1,
              corLamina3: data.corLamina3Andar1,
              padraoLamina1: data.padraoLamina1Andar1,
              padraoLamina2: data.padraoLamina2Andar1,
              padraoLamina3: data.padraoLamina3Andar1,
              processamento: data.processamentoAndar1,
            }} />
            <AndarSection label="Andar 2" data={{
              cor: data.corAndar2,
              posicaoEstoque: data.posicaoEstoqueAndar2,
              corLamina1: data.corLamina1Andar2,
              corLamina2: data.corLamina2Andar2,
              corLamina3: data.corLamina3Andar2,
              padraoLamina1: data.padraoLamina1Andar2,
              padraoLamina2: data.padraoLamina2Andar2,
              padraoLamina3: data.padraoLamina3Andar2,
              processamento: data.processamentoAndar2,
            }} />
            <AndarSection label="Andar 3" data={{
              cor: data.corAndar3,
              posicaoEstoque: data.posicaoEstoqueAndar3,
              corLamina1: data.corLamina1Andar3,
              corLamina2: data.corLamina2Andar3,
              corLamina3: data.corLamina3Andar3,
              padraoLamina1: data.padraoLamina1Andar3,
              padraoLamina2: data.padraoLamina2Andar3,
              padraoLamina3: data.padraoLamina3Andar3,
              processamento: data.processamentoAndar3,
            }} />
          </div>
        </div>
      ) : (
        <div className={styles.empty}>
          <span className={styles.pulse} />
        </div>
      )}
    </div>
  );
}

interface AndarData {
  cor: number;
  posicaoEstoque: number;
  corLamina1: number;
  corLamina2: number;
  corLamina3: number;
  padraoLamina1: number;
  padraoLamina2: number;
  padraoLamina3: number;
  processamento: number;
}

function AndarSection({ label, data }: { label: string; data: AndarData }) {
  return (
    <div className={styles.andar}>
      <span className={styles.andarLabel}>{label}</span>
      <div className={styles.values}>
        <ClpValueField label="cor" value={data.cor} />
        <ClpValueField label="posicaoEstoque" value={data.posicaoEstoque} />
        <ClpValueField label="corLamina1" value={data.corLamina1} />
        <ClpValueField label="corLamina2" value={data.corLamina2} />
        <ClpValueField label="corLamina3" value={data.corLamina3} />
        <ClpValueField label="padraoLamina1" value={data.padraoLamina1} />
        <ClpValueField label="padraoLamina2" value={data.padraoLamina2} />
        <ClpValueField label="padraoLamina3" value={data.padraoLamina3} />
        <ClpValueField label="processamento" value={data.processamento} />
      </div>
    </div>
  );
}
