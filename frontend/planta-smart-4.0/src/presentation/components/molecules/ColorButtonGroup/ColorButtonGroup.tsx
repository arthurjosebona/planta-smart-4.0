import clsx from 'clsx';
import { CorEstoque } from '@enums/CorEstoque';
import styles from '@components/molecules/ColorButtonGroup/colorButtonGroup.module.css';

interface ColorButtonGroupProps {
  onColorChange: (cor: CorEstoque) => void;
  disabled?: boolean;
}

const CORES: { cor: CorEstoque; label: string }[] = [
  { cor: CorEstoque.Preto, label: 'Preto' },
  { cor: CorEstoque.Azul, label: 'Azul' },
  { cor: CorEstoque.Vermelho, label: 'Vermelho' },
  { cor: CorEstoque.Vazio, label: 'Vazio' },
];

const COR_BTN_CLASS: Record<CorEstoque, string> = {
  [CorEstoque.Azul]: styles['color-btn--azul'],
  [CorEstoque.Vermelho]: styles['color-btn--vermelho'],
  [CorEstoque.Preto]: styles['color-btn--preto'],
  [CorEstoque.Vazio]: styles['color-btn--vazio'],
};

export const ColorButtonGroup: React.FC<ColorButtonGroupProps> = ({
  onColorChange,
  disabled = false,
}) => {
  return (
    <div className={styles['color-button-box']} role="group" aria-label="Alterar cor do bloco">
      {CORES.map(({ cor, label }) => (
        <button
          key={cor}
          type="button"
          className={clsx(styles['color-btn'], COR_BTN_CLASS[cor])}
          onClick={() => onColorChange(cor)}
          disabled={disabled}
        >
          {label}
        </button>
      ))}
    </div>
  );
};
