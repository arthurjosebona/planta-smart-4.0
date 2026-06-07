import { SlotExpedicao } from '../../atoms/SlotExpedicao/SlotExpedicao';
import { Expedicao } from '@entities/Expedicao';
import styles from '@components/molecules/ViewExpedicao/viewExpedicao.module.css';

interface ViewExpedicaoProps {
  expedicao: Expedicao[];
}

export const ViewExpedicao: React.FC<ViewExpedicaoProps> = ({ expedicao }) => {
  return (
    <div className={styles['view-expedicao']}>
      {expedicao.map((slot) => (
        <SlotExpedicao key={slot.posicaoFisica} slot={slot} />
      ))}
    </div>
  );
};
