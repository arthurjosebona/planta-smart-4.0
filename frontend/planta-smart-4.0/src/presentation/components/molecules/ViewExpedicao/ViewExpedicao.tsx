import { SlotExpedicao } from '../../atoms/SlotExpedicao/SlotExpedicao';
import { Expedicao } from '@entities/Expedicao';
import styles from '@components/molecules/ViewExpedicao/viewExpedicao.module.css';

interface ViewExpedicaoProps {
  expedicao: Expedicao[];
  editMode?: boolean;
  selectedId?: number | null;
  onToggle?: (id: number) => void;
  onSelect?: (id: number) => void;
}

export const ViewExpedicao: React.FC<ViewExpedicaoProps> = ({
  expedicao,
  editMode = false,
  selectedId = null,
  onToggle,
  onSelect,
}) => {
  return (
    <div className={styles['view-expedicao']}>
      {expedicao.map((slot) => (
        <SlotExpedicao
          key={slot.posicaoFisica}
          slot={slot}
          editMode={editMode}
          selected={selectedId === slot.id}
          onClick={() => onToggle?.(slot.id)}
          onSelect={() => onSelect?.(slot.id)}
        />
      ))}
    </div>
  );
};