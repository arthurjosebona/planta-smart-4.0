import { ReactNode } from 'react';
import styles from './navIcon.module.css';

interface NavIconProps {
  children: ReactNode;
  'aria-hidden'?: boolean;
}

export function NavIcon({ children, 'aria-hidden': ariaHidden = true }: NavIconProps) {
  return (
    <svg
      className={styles.navIcon}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={1.8}
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden={ariaHidden}
    >
      {children}
    </svg>
  );
}
