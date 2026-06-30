import React from 'react';
import styles from '@components/atoms/ActionButton/actionButton.module.css';

interface ActionButtonProps {
  label: string;
  onClick?: () => void;
  variant?: 'ghost' | 'primary';
  disabled?: boolean;
}

export function ActionButton({ label, onClick, variant = 'ghost', disabled = false }: ActionButtonProps) {
  const className = variant === 'primary' ? styles.btnPrimary : styles.btnGhost;

  return (
    <button onClick={onClick} className={className} disabled={disabled}>
      {label}
    </button>
  );
}
