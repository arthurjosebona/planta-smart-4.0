import clsx from 'clsx';
import { CorEstoque } from '@enums/CorEstoque';
import styles from './colorBlock.module.css';

interface ColorBlockProps {
  cor: CorEstoque;
  className?: string;
}

const COR_CLASS: Record<CorEstoque, string> = {
  [CorEstoque.Azul]: styles['color-AZUL'],
  [CorEstoque.Vermelho]: styles['color-VERMELHO'],
  [CorEstoque.Preto]: styles['color-PRETO'],
  [CorEstoque.Vazio]: styles['color-VAZIO'],
};

export const ColorBlock: React.FC<ColorBlockProps> = ({ cor, className }) => {
  return (
    <span className={clsx(styles.block, COR_CLASS[cor], className)} aria-label={`Bloco ${cor}`} />
  );
};
