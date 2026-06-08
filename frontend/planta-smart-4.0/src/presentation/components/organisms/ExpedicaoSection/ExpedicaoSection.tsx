import { Expedicao } from '@entities/Expedicao';
import { ViewExpedicao } from '@components/molecules/ViewExpedicao/ViewExpedicao';
import styles from '@components/organisms/ExpedicaoSection/expedicaoSection.module.css';

interface ExpedicaoSectionProps {
  expedicao: Expedicao[];
}

export const ExpedicaoSection: React.FC<ExpedicaoSectionProps> = ({ expedicao }) => {
  return (
    <section className={styles.expedicao} aria-label="Expedição">
      <h1 className="title">Expedição</h1>
      <ViewExpedicao expedicao={expedicao} />
    </section>
  );
};
