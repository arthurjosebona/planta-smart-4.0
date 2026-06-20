import { useClpStream } from '../../hook/useClpStream';
import type { MonitorModel } from './MonitorModel';

export function useMonitorViewModel(): MonitorModel {
  return useClpStream();
}
