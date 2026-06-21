import { ClpBoolFlag } from '@components/atoms/ClpBoolFlag/ClpBoolFlag';
import styles from './clpFlagGroup.module.css';

export interface FlagEntry {
  label: string;
  value: boolean;
}

interface ClpFlagGroupProps {
  title: string;
  flags: FlagEntry[];
}

export function ClpFlagGroup({ title, flags }: ClpFlagGroupProps) {
  return (
    <div className={styles.group}>
      <span className={styles.title}>{title}</span>
      <div className={styles.grid}>
        {flags.map((f) => (
          <ClpBoolFlag key={f.label} label={f.label} value={f.value} />
        ))}
      </div>
    </div>
  );
}
