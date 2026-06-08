import { Link } from 'react-router-dom';
import styles from './navLink.module.css';

interface NavLinkProps {
  href: string;
  icon: React.ReactNode;
  currentPath: string;
  matchPath?: string;
  children: React.ReactNode;
}

export function NavLink({ href, icon, currentPath, matchPath, children }: NavLinkProps) {
  const isActive =
    currentPath === href ||
    (matchPath ? currentPath.startsWith(matchPath) : false);

  return (
    <Link
      to={href}
      className={`${styles.link} ${isActive ? styles.active : ''}`}
      aria-current={isActive ? 'page' : undefined}
    >
      <svg
        className={styles.icon}
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.75"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
      >
        {icon}
      </svg>
      <span className={styles.label}>{children}</span>
    </Link>
  );
}