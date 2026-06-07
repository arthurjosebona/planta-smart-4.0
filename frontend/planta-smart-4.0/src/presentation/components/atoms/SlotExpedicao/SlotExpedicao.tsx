import clsx from 'clsx';
import { Expedicao } from '@entities/Expedicao';
import styles from '@components/atoms/SlotExpedicao/slotExpedicao.module.css';

interface SlotExpedicaoProps {
  slot: Expedicao;
}

export const SlotExpedicao: React.FC<SlotExpedicaoProps> = ({ slot }) => {
  return (
    <div className={styles.slot}>
      <span className={styles['slot-label']}>#{slot.posicaoFisica}</span>
      {slot.ordemDeProducaoAtual !== null ? (
        <span className={styles['slot-op']}>{slot.ordemDeProducaoAtual}</span>
      ) : (
        <span className={clsx(styles['slot-op'], styles['slot-vazio'])} aria-label="Vazio">
          —
        </span>
      )}
    </div>
  );
};
