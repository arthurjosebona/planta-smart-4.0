interface TitleProps {
  title: string;
}

export default function Title({ title }: TitleProps) {
  return (
    <h1
      style={{
        fontSize: 11,
        fontWeight: 700,
        textTransform: 'uppercase',
        letterSpacing: '0.08em',
        color: '#888',
        marginBottom: 8,
      }}
    >
      {title}
    </h1>
  );
}