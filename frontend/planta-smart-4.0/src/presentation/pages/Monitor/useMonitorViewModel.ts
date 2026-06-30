import { useMonitorContext } from '@contexts/MonitorContext';
import { usePingContext } from '@contexts/PingContext';

export function useMonitorViewModel() {
  const monitor = useMonitorContext();
  const { pingMap } = usePingContext();

  return { ...monitor, pingMap };
}
