import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { routes } from './router/routes';
import { EstoqueProvider } from '@contexts/EstoqueContext';
import { ExpedicaoProvider } from '@contexts/ExpedicaoContext';
import { MonitorProvider } from '@contexts/MonitorContext';
import { PingProvider } from '@contexts/PingContext';

export default function App() {
  return (
    <EstoqueProvider>
      <ExpedicaoProvider>
        <MonitorProvider>
          <PingProvider>
            <BrowserRouter>
              <Routes>
                {routes.map((route) => (
                  <Route key={route.path} path={route.path} element={route.element} />
                ))}
              </Routes>
            </BrowserRouter>
          </PingProvider>
        </MonitorProvider>
      </ExpedicaoProvider>
    </EstoqueProvider>
  );
}
