import React from 'react';
import Title from '@components/atoms/Title/Title';
import styles from '@components/molecules/Section/section.module.css';

interface SectionProps {
  title: string;
  children: React.ReactNode;
}

export default function Section({ title, children }: SectionProps) {
  return (
    <div className={styles.section}>
      <Title title={title} />
      {children}
    </div>
  );
}
