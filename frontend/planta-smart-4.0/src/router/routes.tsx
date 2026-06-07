import DashboardView from '@pages/Dashboard/DashboardView';
import PedidosView from '@pages/Pedidos/PedidosView';
import StoreView from '@pages/Store/StoreView';
import { Navigate } from 'react-router-dom';

export const routes = [
  { path: '/', element: <Navigate to="/store/" replace /> },
  { path: '/store', element: <StoreView /> },
  { path: '/dashboard', element: <DashboardView /> },
  { path: '/pedidos', element: <PedidosView />},
];
