import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { routes } from './router/routes';
import { EstoqueProvider } from '@contexts/EstoqueContext';
import { ExpedicaoProvider } from '@contexts/ExpedicaoContext';

export default function App() {
  return (
    <EstoqueProvider>
      <ExpedicaoProvider>
        <BrowserRouter>
          <Routes>
            {routes.map((route) => (
              <Route key={route.path} path={route.path} element={route.element} />
            ))}
          </Routes>
        </BrowserRouter>
      </ExpedicaoProvider>
    </EstoqueProvider>
  );
}
