export default function ColorSwatch({
  color,
  selected,
  onClick,
  title,
  size = 28,
}: {
  color: string;
  selected: boolean;
  onClick: () => void;
  title: string;
  size?: number;
}) {
  return (
    <button
      title={title}
      onClick={onClick}
      style={{
        width: size,
        height: size,
        borderRadius: 4,
        border: selected ? '2px solid #333' : '2px solid transparent',
        outline: selected ? '2px solid #fff' : 'none',
        outlineOffset: -3,
        background: color,
        cursor: 'pointer',
        padding: 0,
        flexShrink: 0,
        boxShadow: selected ? '0 0 0 3px #333' : '0 1px 3px rgba(0,0,0,0.25)',
        transition: 'box-shadow 0.1s',
      }}
    />
  );
}