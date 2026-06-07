import clsx from 'clsx';
import { ColorBlock } from '../ColorBlock/ColorBlock';
import { Estoque } from '@entities/Estoque';
import styles from '@components/atoms/BlocoSpace/blocoSpace.module.css';

interface BlocoSpaceProps {
  bloco: Estoque;
  editMode: boolean;
  selected: boolean;
  onClick?: () => void;
}

export const BlocoSpace: React.FC<BlocoSpaceProps> = ({ bloco, editMode, selected, onClick }) => {
  return (
    <div
      className={clsx(
        styles.space,
        editMode && styles['space--editable'],
        selected && styles['space--selected']
      )}
      data-posicao={bloco.posicaoFisica}
      onClick={editMode ? onClick : undefined}
      role={editMode ? 'button' : undefined}
      tabIndex={editMode ? 0 : undefined}
      onKeyDown={editMode ? (e) => e.key === 'Enter' && onClick?.() : undefined}
      aria-pressed={editMode ? selected : undefined}
    >
      {selected && <span className={styles.space__check}>✓</span>}
      <ColorBlock cor={bloco.cor} />
      <span className={styles['space-label']}>{bloco.posicaoFisica}</span>
    </div>
  );
};
