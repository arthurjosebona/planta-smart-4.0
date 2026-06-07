import styles from './logoBadge.module.css';

export function LogoBadge() {
  return (
    <div className={styles.logoBox}>
      <span className={styles.logoWordmark}>SENAI</span>
    </div>
  );
}
