import type { MonitorModel } from '@pages/Monitor/MonitorModel';
import { ClpStationCard } from '@components/organisms/ClpStationCard/ClpStationCard';
import styles from './clpMonitorGrid.module.css';

const STATIONS = [
  { key: 'estoque' as const,   label: 'Estoque',   color: '#378ADD' },
  { key: 'processo' as const,  label: 'Processo',  color: '#1D9E75' },
  { key: 'montagem' as const,  label: 'Montagem',  color: '#EF9F27' },
  { key: 'expedicao' as const, label: 'Expedição', color: '#E24B4A' },
];

interface ClpMonitorGridProps {
  model: MonitorModel;
}

export function ClpMonitorGrid({ model }: ClpMonitorGridProps) {
  return (
    <div className={styles.grid}>
      {STATIONS.map(({ key, label, color }) => (
        <ClpStationCard
          key={key}
          label={label}
          color={color}
          data={model[key]}
        />
      ))}
    </div>
  );
}
