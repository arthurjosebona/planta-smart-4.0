import { Expedicao } from '@entities/Expedicao';
import { ViewExpedicao } from '@components/molecules/ViewExpedicao/ViewExpedicao';
import styles from '@components/organisms/ExpedicaoSection/expedicaoSection.module.css';

interface ExpedicaoSectionProps {
  expedicao: Expedicao[];
  editMode: boolean;
  selectedId: number | null;
  opInput: string;
  loading: boolean;
  onEnterEdit: () => void;
  onCancel: () => void;
  onToggleSlot: (id: number) => void;
  onSelectSlot: (id: number) => void;
  onOpInputChange: (value: string) => void;
  onSave: () => void;
}

export const ExpedicaoSection: React.FC<ExpedicaoSectionProps> = ({
  expedicao,
  editMode,
  selectedId,
  opInput,
  loading,
  onEnterEdit,
  onCancel,
  onToggleSlot,
  onSelectSlot,
  onOpInputChange,
  onSave,
}) => {
  return (
    <section className={styles.expedicao} aria-label="Expedição">
      <h1 className="title">Expedição</h1>

      <ViewExpedicao
        expedicao={expedicao}
        editMode={editMode}
        selectedId={selectedId}
        onToggle={onToggleSlot}
        onSelect={onSelectSlot}
      />

      {editMode && selectedId !== null && (
        <div className={styles['op-input-row']}>
          <label className={styles['op-label']} htmlFor="op-input">
            OP
          </label>
          <input
            id="op-input"
            type="number"
            min={0}
            className={styles['op-input']}
            value={opInput}
            onChange={(e) => onOpInputChange(e.target.value)}
            placeholder="Nº da OP"
            disabled={loading}
            autoFocus
          />
        </div>
      )}

      <div className={styles['button-box']}>
        {!editMode ? (
          <button type="button" className={styles.edit} onClick={onEnterEdit}>
            Editar Expedição
          </button>
        ) : (
          <>
            <button
              type="button"
              className={styles.cancel}
              onClick={onCancel}
              disabled={loading}
            >
              Cancelar
            </button>
            <button
              type="button"
              className={styles.save}
              onClick={onSave}
              disabled={loading}
            >
              {loading ? 'Salvando…' : 'Salvar'}
            </button>
          </>
        )}
      </div>
    </section>
  );
};