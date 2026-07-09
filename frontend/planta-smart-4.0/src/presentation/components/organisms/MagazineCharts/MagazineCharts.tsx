import { Estoque } from '@entities/Estoque';
import { Expedicao } from '@entities/Expedicao';
import { CorEstoque } from '@enums/CorEstoque';
import styles from './magazineCharts.module.css';

interface Slice {
  value: number;
  color: string;
  label: string;
}

function polarToXY(cx: number, cy: number, r: number, angle: number): [number, number] {
  return [cx + r * Math.cos(angle), cy + r * Math.sin(angle)];
}

function describeSlice(cx: number, cy: number, r: number, start: number, end: number): string {
  const span = end - start;
  if (span >= Math.PI * 2 - 0.001) {
    const [x1, y1] = polarToXY(cx, cy, r, start);
    const [x2, y2] = polarToXY(cx, cy, r, start + Math.PI);
    return `M ${cx} ${cy} L ${x1} ${y1} A ${r} ${r} 0 0 1 ${x2} ${y2} A ${r} ${r} 0 0 1 ${x1} ${y1} Z`;
  }
  const [sx, sy] = polarToXY(cx, cy, r, start);
  const [ex, ey] = polarToXY(cx, cy, r, end);
  return `M ${cx} ${cy} L ${sx} ${sy} A ${r} ${r} 0 ${span > Math.PI ? 1 : 0} 1 ${ex} ${ey} Z`;
}

function PieChart({ slices, title }: { slices: Slice[]; title: string }) {
  const SIZE = 130;
  const CX = SIZE / 2;
  const CY = SIZE / 2;
  const R = SIZE / 2 - 5;
  const total = slices.reduce((s, sl) => s + sl.value, 0);

  const paths: { d: string; color: string; label: string; value: number }[] = [];
  let angle = -Math.PI / 2;

  for (const sl of slices) {
    if (sl.value === 0) continue;
    const sweep = (sl.value / total) * Math.PI * 2;
    paths.push({
      d: describeSlice(CX, CY, R, angle, angle + sweep),
      color: sl.color,
      label: sl.label,
      value: sl.value,
    });
    angle += sweep;
  }

  return (
    <div className={styles.chart}>
      <p className={styles['chart-title']}>{title}</p>
      {total === 0 ? (
        <p className={styles['chart-empty']}>Sem dados</p>
      ) : (
        <>
          <svg className={styles.svg} viewBox={`0 0 ${SIZE} ${SIZE}`} aria-hidden>
            {paths.map((p, i) => (
              <path key={i} d={p.d} fill={p.color} stroke="#161719" strokeWidth={2} />
            ))}
          </svg>
          <ul className={styles.legend}>
            {slices
              .filter((s) => s.value > 0)
              .map((s, i) => (
                <li key={i} className={styles['legend-item']}>
                  <span
                    className={styles['legend-dot']}
                    style={{ backgroundColor: s.color }}
                  />
                  <span className={styles['legend-label']}>{s.label}</span>
                  <span className={styles['legend-value']}>{s.value}</span>
                </li>
              ))}
          </ul>
        </>
      )}
    </div>
  );
}

const ESTOQUE_CORES: { cor: CorEstoque; color: string; label: string }[] = [
  { cor: CorEstoque.Azul, color: '#1a55cc', label: 'Azul' },
  { cor: CorEstoque.Vermelho, color: '#cc2222', label: 'Vermelho' },
  { cor: CorEstoque.Preto, color: '#3a3a3c', label: 'Preto' },
  { cor: CorEstoque.Vazio, color: '#B1AEAF', label: 'Vazio' },
];

interface MagazineChartsProps {
  estoque: Estoque[];
  expedicao: Expedicao[];
}

export function MagazineCharts({ estoque, expedicao }: MagazineChartsProps) {
  const colorSlices: Slice[] = ESTOQUE_CORES.map(({ cor, color, label }) => ({
    value: estoque.filter((e) => e.cor === cor).length,
    color,
    label,
  }));

  const estOcupado = estoque.filter((e) => e.cor !== CorEstoque.Vazio).length;
  const estVazio = estoque.length - estOcupado;

  const expOcupado = expedicao.filter((e) => e.ordemDeProducaoAtual !== null).length;
  const expVazio = expedicao.length - expOcupado;

  return (
    <aside className={styles.panel}>
      <PieChart title="Cores — Estoque" slices={colorSlices} />
      <div className={styles.divider} />
      <PieChart
        title="Ocupação — Estoque"
        slices={[
          { value: estOcupado, color: '#4f8ef7', label: 'Ocupado' },
          { value: estVazio, color: '#B1AEAF', label: 'Vazio' },
        ]}
      />
      <div className={styles.divider} />
      <PieChart
        title="Ocupação — Expedição"
        slices={[
          { value: expOcupado, color: '#4caf7d', label: 'Com OP' },
          { value: expVazio, color: '#B1AEAF', label: 'Vazio' },
        ]}
      />
    </aside>
  );
}
