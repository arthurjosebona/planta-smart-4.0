import { LogoBadge } from '@components/atoms/LogoBadge/LogoBadge';
import styles from '@components/molecules/SiteTitle/siteTitle.module.css';

interface SiteTitleProps {
  title: string;
  highlight?: string;
}

export function SiteTitle({ title, highlight }: SiteTitleProps) {
  const base = highlight ? title.replace(highlight, '') : title;

  return (
    <div className={styles.headerTitle}>
      <LogoBadge />
      <h1>
        {base}
        {highlight && <span>{highlight}</span>}
      </h1>
    </div>
  );
}
