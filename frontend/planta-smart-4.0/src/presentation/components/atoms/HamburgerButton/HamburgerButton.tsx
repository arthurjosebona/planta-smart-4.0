import styles from './hamburgerButton.module.css';

interface HamburgerButtonProps {
  open: boolean;
  onClick: () => void;
}

export function HamburgerButton({ open, onClick }: HamburgerButtonProps) {
  return (
    <button
      className={`${styles.hamburger}${open ? ` ${styles.open}` : ''}`}
      onClick={onClick}
      aria-label="Abrir menu"
      aria-expanded={open}
      aria-controls="navbarMenu"
    >
      <span />
      <span />
      <span />
    </button>
  );
}
