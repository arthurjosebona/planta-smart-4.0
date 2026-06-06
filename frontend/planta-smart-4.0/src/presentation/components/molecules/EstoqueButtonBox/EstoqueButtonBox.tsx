import styles from './estoqueButtonBox.module.css';

interface EstoqueButtonBoxProps {
  editMode: boolean;
  loading: boolean;
  onEdit: () => void;
  onClean: () => void;
  onCancel: () => void;
  onSave: () => void;
}

export const EstoqueButtonBox: React.FC<EstoqueButtonBoxProps> = ({
  editMode,
  loading,
  onEdit,
  onClean,
  onCancel,
  onSave,
}) => {
  if (!editMode) {
    return (
      <div className={styles['button-box']}>
        <button type="button" className={styles.edit} onClick={onEdit}>
          Editar Estoque
        </button>
      </div>
    );
  }

  return (
    <div className={styles['button-box']}>
      <div className={styles['button-box__group']}>
        <button type="button" className={styles.clear} onClick={onClean} disabled={loading}>
          Limpar
        </button>
        <button type="button" className={styles.cancel} onClick={onCancel} disabled={loading}>
          Cancelar
        </button>
      </div>
      <button type="button" className={styles.save} onClick={onSave} disabled={loading}>
        {loading ? 'Salvando…' : 'Salvar'}
      </button>
    </div>
  );
};
