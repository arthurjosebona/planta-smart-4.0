import React from 'react';
import styles from '@components/templates/AppTemplate.module.css';

export interface AppTemplateProps {
  children: React.ReactNode;
}

export const AppTemplate: React.FC<AppTemplateProps> = ({ children }) => {
  return (
    <div className={styles.root}>
      <div className={styles.container}>{children}</div>
    </div>
  );
};
