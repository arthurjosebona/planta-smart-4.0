import Title from '../atoms/Title';

interface SectionProps {
  title: string;
  children: React.ReactNode;
}

export default function Section({ title, children }: SectionProps) {
  return (
    <div style={{ marginBottom: 16 }}>
      <div>
        <Title title={title} />
      </div>
      {children}
    </div>
  );
}
