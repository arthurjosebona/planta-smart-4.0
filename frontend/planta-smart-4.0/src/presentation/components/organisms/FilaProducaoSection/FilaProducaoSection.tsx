import { PedidoCard } from '@components/organisms/PedidoCard/PedidoCard';
import { ProducaoTimer } from '@components/atoms/ProducaoTimer/ProducaoTimer';
import { useFilaProducaoContext } from '@contexts/FilaProducaoContext';
import styles from '@components/organisms/FilaProducaoSection/filaProducaoSection.module.css';

interface FilaProducaoSectionProps {
  onSelecionarPedido?: (id: number) => void;
}

export function FilaProducaoSection({ onSelecionarPedido }: FilaProducaoSectionProps) {
  const { fila, tempoExecucaoSegundos, conectado } = useFilaProducaoContext();
  const { emExecucao, pendentes } = fila;

  return (
    <section className={styles.section} aria-label="Fila de Produção">
      <div aria-hidden="true" className={styles.accentLine} />

      <div className={styles.sectionHeader}>
        <h2 className={styles.heading}>Fila de Produção</h2>
        <span
          className={`${styles.conexao} ${conectado ? styles.conexaoOn : styles.conexaoOff}`}
        >
          {conectado ? 'Conectado' : 'Desconectado'}
        </span>
      </div>

      {/* Pedido em execução */}
      <div className={styles.block}>
        <div className={styles.blockHeader}>
          <span className={styles.blockLabel}>Em produção</span>
          {emExecucao && <ProducaoTimer segundos={tempoExecucaoSegundos} />}
        </div>

        {emExecucao ? (
          <PedidoCard
            pedido={emExecucao}
            onClick={() => onSelecionarPedido?.(emExecucao.id!)}
          />
        ) : (
          <p className={`${styles.stateMsg} ${styles.stateMsgEmpty}`}>
            Nenhum pedido em produção.
          </p>
        )}
      </div>

      {/* Fila de pendentes */}
      <div className={styles.block}>
        <div className={styles.blockHeader}>
          <span className={styles.blockLabel}>Pendentes</span>
          <span className={styles.counter}>{pendentes.length}</span>
        </div>

        {pendentes.length === 0 ? (
          <p className={`${styles.stateMsg} ${styles.stateMsgEmpty}`}>
            Nenhum pedido na fila.
          </p>
        ) : (
          <ol className={styles.list}>
            {pendentes.map((pedido, i) => (
              <li key={pedido.id} className={styles.item}>
                <span className={styles.posicao}>{i + 1}º</span>
                <div className={styles.itemCard}>
                  <PedidoCard
                    pedido={pedido}
                    onClick={() => onSelecionarPedido?.(pedido.id!)}
                  />
                </div>
              </li>
            ))}
          </ol>
        )}
      </div>
    </section>
  );
}
