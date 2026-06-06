import { BlocoSpace } from '../../atoms/BlocoSpace/BlocoSpace';
import { Estoque } from '@entities/Estoque';
import styles from './viewEstoque.module.css';

interface ViewEstoqueProps {
  estoque: Estoque[];
  editMode: boolean;
  selectedIds: number[];
  onToggle: (id: number) => void;
}

export const ViewEstoque: React.FC<ViewEstoqueProps> = ({
  estoque,
  editMode,
  selectedIds,
  onToggle,
}) => {
  return (
    <div className={styles['view-estoque']}>
      {estoque.map((bloco) => (
        <BlocoSpace
          key={bloco.id}
          bloco={bloco}
          editMode={editMode}
          selected={selectedIds.includes(bloco.id)}
          onClick={() => onToggle(bloco.id)}
        />
      ))}
    </div>
  );
};
