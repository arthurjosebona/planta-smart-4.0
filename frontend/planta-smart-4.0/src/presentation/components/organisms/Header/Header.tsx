import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { SiteTitle } from '@components/molecules/SiteTitle/SiteTitle';
import { NavLink } from '@components/molecules/NavLink/NavLink';
import { NavSeparator } from '@components/atoms/NavSeparator/NavSeparator';
import { HamburgerButton } from '@components/atoms/HamburgerButton/HamburgerButton';
import styles from '@components/organisms/Header/header.module.css';

const IconHome = (
  <>
    <path d="m3 9 9-7 9 7" />
    <path d="M5 10v10a1 1 0 0 0 1 1h3v-6h6v6h3a1 1 0 0 0 1-1V10" />
  </>
);
const IconPedido = (
  <>
    <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2" />
    <rect x="9" y="3" width="6" height="4" rx="2" />
    <path d="M9 12h6M9 16h4" />
  </>
);
const IconStore = (
  <>
    <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z" />
    <line x1="3" y1="6" x2="21" y2="6" />
    <path d="M16 10a4 4 0 0 1-8 0" />
  </>
);
const IconEstacoes = (
  <>
    <rect x="2" y="7" width="6" height="10" rx="1" />
    <rect x="9" y="3" width="6" height="14" rx="1" />
    <rect x="16" y="10" width="6" height="7" rx="1" />
    <line x1="2" y1="21" x2="22" y2="21" />
  </>
);
const IconDashboard = (
  <>
    <rect x="3" y="3" width="7" height="7" rx="1" />
    <rect x="14" y="3" width="7" height="7" rx="1" />
    <rect x="3" y="14" width="7" height="7" rx="1" />
    <rect x="14" y="14" width="7" height="7" rx="1" />
  </>
);

interface NavItem {
  href: string;
  label: string;
  icon: React.ReactNode;
  matchPath?: string;
}
const NAV_ITEMS: (NavItem | 'separator')[] = [
  { href: '/', label: 'Home', icon: IconHome},
  { href: '/store',     label: 'Store',     icon: IconStore     },
  { href: '/pedidos',   label: 'Pedidos',   icon: IconPedido    },
  'separator',
  { href: '/dashboard', label: 'Dashboard', icon: IconDashboard },
  { href: '/estacoes',  label: 'Estações',  icon: IconEstacoes  },
  

];

export function Header() {
  const [menuOpen, setMenuOpen] = useState(false);
  const { pathname } = useLocation();
  const navRef    = useRef<HTMLElement>(null);
  const btnWrapRef = useRef<HTMLDivElement>(null);

  // Fecha ao navegar para outra rota
  useEffect(() => { setMenuOpen(false); }, [pathname]);

  // Fecha ao clicar fora
  useEffect(() => {
    function onMouseDown(e: MouseEvent) {
      if (
        menuOpen &&
        !navRef.current?.contains(e.target as Node) &&
        !btnWrapRef.current?.contains(e.target as Node)
      ) setMenuOpen(false);
    }
    document.addEventListener('mousedown', onMouseDown);
    return () => document.removeEventListener('mousedown', onMouseDown);
  }, [menuOpen]);

  // Fecha com Escape
  useEffect(() => {
    function onKeyDown(e: KeyboardEvent) {
      if (e.key === 'Escape' && menuOpen) setMenuOpen(false);
    }
    document.addEventListener('keydown', onKeyDown);
    return () => document.removeEventListener('keydown', onKeyDown);
  }, [menuOpen]);

  return (
    <header className={styles.siteHeader}>
      <div className={styles.inner}>
        <SiteTitle title="Smart 4.0" />

        <nav
          id="navbarMenu"
          ref={navRef}
          className={`${styles.nav}${menuOpen ? ` ${styles.open}` : ''}`}
          aria-label="Navegação principal"
        >
          {NAV_ITEMS.map((item, i) =>
            item === 'separator' ? (
              <NavSeparator key={`sep-${i}`} />
            ) : (
              <NavLink
                key={item.href}
                href={item.href}
                icon={item.icon}
                matchPath={item.matchPath}
                currentPath={pathname}
              >
                {item.label}
              </NavLink>
            ),
          )}
        </nav>

        {/* div wrapper: dispensa forwardRef no HamburgerButton */}
        <div ref={btnWrapRef} className={styles.menuBtn}>
          <HamburgerButton
            open={menuOpen}
            aria-controls="navbarMenu"
            aria-expanded={menuOpen}
            onClick={() => setMenuOpen((p) => !p)}
          />
        </div>
      </div>
    </header>
  );
}