import { ReactNode } from 'react';
import styles from '@components/template/appTemplate.module.css';
import { Header } from '@components/organisms/Header/Header';

export interface AppTemplateProps {
  children: ReactNode;
}

export function AppTemplate({ children }: AppTemplateProps) {
  return (
    <div className={styles.root}>
      <Header />
      <div className={styles.container}>{children}</div>
    </div>
  );
}
