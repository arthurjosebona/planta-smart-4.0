import React from 'react';

interface ActionButtonProps {
  label: string;
  onClick?: () => void;
  variant?: 'ghost' | 'primary';
}

export const ActionButton: React.FC<ActionButtonProps> = ({
  label,
  onClick,
  variant = 'ghost',
}) => {
  if (variant === 'primary') {
    return (
      <button
        onClick={onClick}
        style={{
          display: 'inline-flex',
          alignItems: 'center',
          padding: '8px 18px',
          background: 'var(--color-accent)',
          color: '#fff',
          border: '1px solid var(--color-accent)',
          fontSize: 13,
          fontFamily: 'var(--font-sans)',
          fontWeight: 600,
          borderRadius: 'var(--radius-sm)',
          letterSpacing: '0.02em',
          cursor: 'pointer',
          transition: 'background 0.14s, border-color 0.14s',
        }}
      >
        {label}
      </button>
    );
  }

  return (
    <button
      onClick={onClick}
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        padding: '5px 12px',
        borderRadius: 'var(--radius-sm)',
        fontSize: 11,
        fontFamily: 'var(--font-mono)',
        fontWeight: 500,
        letterSpacing: '0.04em',
        cursor: 'pointer',
        border: '1px solid var(--color-border-bright)',
        background: 'transparent',
        color: 'var(--color-text-dim)',
        transition: 'background 0.13s, color 0.13s, border-color 0.13s',
      }}
    >
      {label}
    </button>
  );
};
