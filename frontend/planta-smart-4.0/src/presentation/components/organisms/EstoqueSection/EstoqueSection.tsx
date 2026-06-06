import { CorEstoque } from '@enums/CorEstoque';
import { Estoque } from '@entities/Estoque';
import { EstoqueButtonBox } from '@components/molecules/EstoqueButtonBox/EstoqueButtonBox';
import { ColorButtonGroup } from '@components/molecules/ColorButtonGroup/ColorButtonGroup';
import { ViewEstoque } from '@components/molecules/ViewEstoque/ViewEstoque';
import styles from './estoqueSection.module.css';

interface EstoqueSectionProps {
  estoque: Estoque[];
  editMode: boolean;
  selectedIds: number[];
  loading: boolean;
  onEnterEdit: () => void;
  onCancel: () => void;
  onToggleBloco: (id: number) => void;
  onChangeColor: (cor: CorEstoque) => void;
  onClean: () => void;
  onSave: () => void;
}

export const EstoqueSection: React.FC<EstoqueSectionProps> = ({
  estoque,
  editMode,
  selectedIds,
  loading,
  onEnterEdit,
  onCancel,
  onToggleBloco,
  onChangeColor,
  onClean,
  onSave,
}) => {
  return (
    <section className={styles.estoque} aria-label="Estoque">
      <h1 className="title">Estoque</h1>
      {editMode && (
        <ColorButtonGroup
          onColorChange={onChangeColor}
          disabled={selectedIds.length === 0 || loading}
        />
      )}
      <ViewEstoque
        estoque={estoque}
        editMode={editMode}
        selectedIds={selectedIds}
        onToggle={onToggleBloco}
      />
      <EstoqueButtonBox
        editMode={editMode}
        loading={loading}
        onEdit={onEnterEdit}
        onClean={onClean}
        onCancel={onCancel}
        onSave={onSave}
      />
    </section>
  );
};
