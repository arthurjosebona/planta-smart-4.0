import clsx from 'clsx';
import { Expedicao } from '@entities/Expedicao';
import styles from '@components/atoms/SlotExpedicao/slotExpedicao.module.css';

interface SlotExpedicaoProps {
  slot: Expedicao;
  editMode?: boolean;
  selected?: boolean;
  onClick?: () => void;
  onSelect?: () => void;
}

export const SlotExpedicao: React.FC<SlotExpedicaoProps> = ({
  slot,
  editMode = false,
  selected = false,
  onClick,
  onSelect,
}) => {
  const handleClick = editMode ? onClick : onSelect;
  const isClickable = Boolean(handleClick);

  return (
    <div
      className={clsx(
        styles.slot,
        editMode && styles['slot--edit'],
        !editMode && isClickable && styles['slot--clickable'],
        selected && styles['slot--selected'],
      )}
      onClick={handleClick}
      role={isClickable ? 'button' : undefined}
      tabIndex={isClickable ? 0 : undefined}
      onKeyDown={isClickable && handleClick ? (e) => e.key === 'Enter' && handleClick() : undefined}
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