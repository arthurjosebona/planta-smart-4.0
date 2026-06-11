import clsx from 'clsx';
import { Expedicao } from '@entities/Expedicao';
import styles from '@components/atoms/SlotExpedicao/slotExpedicao.module.css';

interface SlotExpedicaoProps {
  slot: Expedicao;
  editMode?: boolean;
  selected?: boolean;
  onClick?: () => void;
}

export const SlotExpedicao: React.FC<SlotExpedicaoProps> = ({
  slot,
  editMode = false,
  selected = false,
  onClick,
}) => {
  return (
    <div
      className={clsx(
        styles.slot,
        editMode && styles['slot--edit'],
        selected && styles['slot--selected'],
      )}
      onClick={editMode ? onClick : undefined}
      role={editMode ? 'button' : undefined}
      tabIndex={editMode ? 0 : undefined}
      onKeyDown={editMode && onClick ? (e) => e.key === 'Enter' && onClick() : undefined}
    >
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