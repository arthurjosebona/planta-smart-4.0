import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { routes } from './router/routes';
import { EstoqueProvider } from '@contexts/EstoqueContext';
import { ExpedicaoProvider } from '@contexts/ExpedicaoContext';
import { MonitorProvider } from '@contexts/MonitorContext';
import { PingProvider } from '@contexts/PingContext';
import { StatusProvider } from '@contexts/StatusContext';

export default function App() {
  return (
    <EstoqueProvider>
      <ExpedicaoProvider>
        <MonitorProvider>
          <StatusProvider>
            <PingProvider>
              <BrowserRouter>
                <Routes>
                  {routes.map((route) => (
                    <Route key={route.path} path={route.path} element={route.element} />
                  ))}
                </Routes>
              </BrowserRouter>
            </PingProvider>
          </StatusProvider>
        </MonitorProvider>
      </ExpedicaoProvider>
    </EstoqueProvider>
  );
}
