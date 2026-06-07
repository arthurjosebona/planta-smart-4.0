import styles from './ColorSwatch.module.css';

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
      className={`${styles.swatch} ${selected ? styles.swatchSelected : ''}`}
      style={{
        width: size,
        height: size,
        background: color,
      }}
    />
  );
}