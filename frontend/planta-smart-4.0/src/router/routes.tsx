import StoreView from '../presentation/pages/Store/StoreView';
import { Navigate } from 'react-router-dom';

export const routes = [
  { path: '/', element: <Navigate to="/store/" replace /> },
  { path: '/store', element: <StoreView /> },
];
