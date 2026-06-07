import { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { SiteTitle } from '@components/molecules/SiteTitle/SiteTitle';
import { NavLink } from '@components/molecules/NavLink/NavLink';
import { NavSeparator } from '@components/atoms/NavSeparator/NavSeparator';
import { HamburgerButton } from '@components/atoms/HamburgerButton/HamburgerButton';
import styles from '@components/organisms/Header/header.module.css';

// ─── Ícones ───────────────────────────────────────────────────────────────────

const IconPedido = (
  <>
    <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2" />
    <rect x="9" y="3" width="6" height="4" rx="2" />
    <path d="M9 12h6M9 16h4" />
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
    <rect x="2" y="7" width="6" height="10" rx="1" />
    <rect x="9" y="3" width="6" height="14" rx="1" />
    <rect x="16" y="10" width="6" height="7" rx="1" />
    <line x1="2" y1="21" x2="22" y2="21" />
  </>
);

// ─── Rotas ────────────────────────────────────────────────────────────────────

interface NavItem {
  href: string;
  label: string;
  icon: React.ReactNode;
  matchPath?: string;
}

const NAV_ITEMS: (NavItem | 'separator')[] = [
  { href: '/store', label: 'Store', icon: IconPedido },
  { href: '/pedidos', label: 'Pedidos', icon: IconPedido },
  'separator',
  { href: '/dashboard', label: 'Dashboard', icon: IconDashboard },
  { href: '/estacoes', label: 'Estações', icon: IconEstacoes },
];

// ─── Organismo ────────────────────────────────────────────────────────────────

export function Header() {
  const [menuOpen, setMenuOpen] = useState(false);
  const { pathname } = useLocation();
  const navRef = useRef<HTMLElement>(null);
  const btnRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (
        menuOpen &&
        !navRef.current?.contains(e.target as Node) &&
        !btnRef.current?.contains(e.target as Node)
      ) {
        setMenuOpen(false);
      }
    }
    document.addEventListener('click', handleClickOutside);
    return () => document.removeEventListener('click', handleClickOutside);
  }, [menuOpen]);

  return (
    <header className={styles.siteHeader}>
      <div className={styles.headerTop}>
        <SiteTitle title="Sistema de Gerenciamento — Bancada Smart 4.0" />
        <HamburgerButton open={menuOpen} onClick={() => setMenuOpen((prev) => !prev)} />
      </div>

      <nav
        id="navbarMenu"
        ref={navRef}
        className={`${styles.headerNav}${menuOpen ? ` ${styles.open}` : ''}`}
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
          )
        )}
      </nav>
    </header>
  );
}
