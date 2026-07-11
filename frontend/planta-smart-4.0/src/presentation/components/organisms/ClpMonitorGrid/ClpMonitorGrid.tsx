import type { MonitorModel } from '@pages/Monitor/MonitorModel';
import type { PingMap } from '@contexts/PingContext';
import { ClpStationCard } from '@components/organisms/ClpStationCard/ClpStationCard';
import { InfoPedidoCard } from '@components/organisms/InfoPedidoCard/InfoPedidoCard';
import styles from './clpMonitorGrid.module.css';

const STATIONS = [
  { key: 'estoque' as const,   label: 'Estoque',   color: '#378ADD' },
  { key: 'processo' as const,  label: 'Processo',  color: '#1D9E75' },
  { key: 'montagem' as const,  label: 'Montagem',  color: '#EF9F27' },
  { key: 'expedicao' as const, label: 'Expedição', color: '#E24B4A' },
];

interface ClpMonitorGridProps {
  model: MonitorModel;
  pingMap: PingMap;
}

export function ClpMonitorGrid({ model, pingMap }: ClpMonitorGridProps) {
  return (
    <div className={styles.grid}>
      {STATIONS.map(({ key, label, color }) => (
        <ClpStationCard
          key={key}
          label={label}
          color={color}
          data={model[key]}
          online={pingMap[key]}
        />
      ))}
      <div className={styles.pedidoRow}>
        <InfoPedidoCard data={model.estoque} />
      </div>
    </div>
  );
}
