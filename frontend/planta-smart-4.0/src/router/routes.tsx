import DashboardView from '@pages/Dashboard/DashboardView';
import IpsView from '@pages/Ips/IpsView';
import PedidosView from '@pages/Pedidos/PedidosView';
import StoreView from '@pages/Store/StoreView';
import EstacoesView from '@pages/Estacoes/EstacoesView';
import MonitorView from '@pages/Monitor/MonitorView';
import { Navigate } from 'react-router-dom';

export const routes = [
  { path: '/', element: <Navigate to="/ips/" replace /> },
  { path: '/store', element: <StoreView /> },
  { path: '/dashboard', element: <DashboardView /> },
  { path: '/pedidos', element: <PedidosView /> },
  { path: '/ips', element: <IpsView /> },
  { path: '/estacoes', element: <EstacoesView /> },
  { path: '/monitor', element: <MonitorView /> },
];
