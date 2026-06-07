import { ReactNode } from 'react';
import { NavIcon } from '../../atoms/NavIcon/NavIcon';

interface NavLinkProps {
  href: string;
  icon: ReactNode;
  children: ReactNode;
  matchPath?: string;
  currentPath: string;
}

export function NavLink({ href, icon, children, matchPath, currentPath }: NavLinkProps) {
  const match = matchPath ?? href;
  const isActive = currentPath.startsWith(match);

  return (
    <a href={href} className={`nav-link${isActive ? ' active' : ''}`}>
      <NavIcon>{icon}</NavIcon>
      {children}
    </a>
  );
}
