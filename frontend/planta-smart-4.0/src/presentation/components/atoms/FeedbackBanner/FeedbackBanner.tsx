import React from 'react';
import styles from './feedbackBanner.module.css';

type FeedbackVariant = 'error' | 'success';

interface FeedbackBannerProps {
  variant: FeedbackVariant;
  message: string;
  onDismiss?: () => void;
}

export function FeedbackBanner({ variant, message, onDismiss }: FeedbackBannerProps) {
  return (
    <div
      className={`${styles.banner} ${variant === 'error' ? styles.bannerError : styles.bannerSuccess}`}
      role="alert"
      aria-live="polite"
    >
      <span className={styles.icon}>{variant === 'error' ? '✕' : '✓'}</span>
      <span className={styles.message}>{message}</span>
      {onDismiss && (
        <button className={styles.dismiss} onClick={onDismiss} aria-label="Fechar">
          ✕
        </button>
      )}
    </div>
  );
}