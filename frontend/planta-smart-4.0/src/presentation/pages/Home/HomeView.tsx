import { AppTemplate } from '@components/template/AppTemplate';
import { StatusEstacao } from '@enums/StatusEstacao';
import { useHomeViewModel } from '@pages/Home/useHomeViewModel';
import styles from './homeView.module.css';
import bancada from '@assets/bancada/Smart40.png';
import produto from '@assets/bloco/HomeBloco.png';

/** Rótulo e tom (classe de cor) para cada status operacional de estação. */
const STATUS_META: Record<StatusEstacao, { label: string; tone: string }> = {
  [StatusEstacao.Parado]: { label: 'Parado', tone: 'muted' },
  [StatusEstacao.Ocupado]: { label: 'Ocupado', tone: 'active' },
  [StatusEstacao.Aguardando]: { label: 'Aguardando', tone: 'warn' },
  [StatusEstacao.Manual]: { label: 'Manual', tone: 'warn' },
  [StatusEstacao.Emergencia]: { label: 'Emergência', tone: 'danger' },
};

export default function HomeView() {
  const { conectado, stations, summary } = useHomeViewModel();

  const kpis = [
    {
      label: 'API REST',
      value: summary.restConectado ? 'Online' : 'Offline',
      tone: summary.restConectado ? 'good' : 'bad',
    },
    {
      label: 'Stream (SSE)',
      value: summary.streamConectado ? 'Ativo' : 'Inativo',
      tone: summary.streamConectado ? 'good' : 'bad',
    },
    {
      label: 'Estações online',
      value: `${summary.estacoesOnline}/${summary.estacoesTotal}`,
      tone: summary.estacoesOnline === summary.estacoesTotal ? 'good' : 'warn',
    },
    {
      label: 'Estoque ocupado',
      value: `${summary.estoqueOcupado}/${summary.estoqueTotal || '—'}`,
      tone: 'neutral',
    },
    {
      label: 'Expedição',
      value: `${summary.expedicaoOcupada}/${summary.expedicaoTotal || '—'}`,
      tone: 'neutral',
    },
    {
      label: 'Modo',
      value: summary.readOnly ? 'Somente leitura' : 'Operação',
      tone: summary.readOnly ? 'warn' : 'good',
    },
  ];

  return (
    <AppTemplate>
      <main id="main-content" className={styles.main}>
        {/* ── Apresentação (base: diagrama da planta) ── */}
        <header className={styles.intro}>
          <span className={styles.eyebrow}>dbSmart40</span>
          <h1 className={styles.title}>
            Sistema de Gerenciamento e Controle da Planta SMART 4.0
          </h1>
          <p className={styles.subtitle}>
            Gerencie ordens de produção, monitore a montagem de blocos e lâminas e controle a
            logística de expedição em uma aplicação Web moderna e escalável.
          </p>
        </header>

        <section className={styles.flow}>
          <div className={styles.flowBancada}>
            <img src={bancada} alt="Bancada Smart 4.0" className={styles.flowImg} />
            <span className={styles.flowCaption}>Planta SMART 4.0</span>
          </div>

          <svg className={styles.flowArrow} viewBox="0 0 48 24" aria-hidden="true">
            <path
              d="M2 12h36m0 0-8-8m8 8-8 8"
              fill="none"
              stroke="currentColor"
              strokeWidth="2.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>

          <img src={produto} alt="Produto — bloco montado" className={styles.produtoImg} />
        </section>

        {/* ── Contexto: o desafio da Indústria 4.0 ── */}
        <section className={styles.context}>
          <h2 className={styles.contextTitle}>Contexto: O Desafio da Indústria 4.0</h2>
          <p className={styles.contextText}>
            A <strong>Indústria 4.0</strong> representa a quarta revolução industrial, onde a
            digitalização, a conectividade e a inteligência de dados transformam o chão de fábrica
            em um ecossistema inteligente. Nesse cenário, não basta apenas produzir; é necessário
            rastrear cada componente em tempo real, otimizar estoques e garantir que a informação
            flua sem barreiras entre as máquinas e os Sistemas de Gestão.
          </p>
          <p className={styles.contextText}>
            <strong>O Desafio:</strong> a planta <strong>SMART 4.0</strong> opera com alta
            flexibilidade e customização de produtos (pedidos simples, duplos ou triplos, conforme
            a solicitação do cliente) e conta com este ecossistema digital{' '}
            <strong>dbSmart40</strong> para que o operador gerencie ordens de produção, monitore a
            montagem de blocos e lâminas, e controle a logística de expedição.
          </p>
        </section>

        {/* ── Indicadores ── */}
        <section className={styles.kpis}>
          {kpis.map((kpi) => (
            <div key={kpi.label} className={`${styles.kpi} ${styles[kpi.tone]}`}>
              <span className={styles.kpiLabel}>{kpi.label}</span>
              <span className={styles.kpiValue}>{kpi.value}</span>
            </div>
          ))}
        </section>

        {/* ── Detalhes das estações ── */}
        <section className={styles.details}>
          <div className={styles.detailsHeader}>
            <span className={styles.sectionLabel}>Estações</span>
            <span
              className={`${styles.connState} ${conectado ? styles.online : styles.offline}`}
            >
              <span className={styles.connDot} />
              {conectado ? 'Conectada' : 'Desconectada'}
            </span>
          </div>

          <ul className={styles.stations}>
            {stations.map((station) => {
              const meta = station.status ? STATUS_META[station.status] : null;
              return (
                <li key={station.key} className={`${styles.stationCard} ${styles[station.key]}`}>
                  <div className={styles.stationTop}>
                    <span className={styles.stationLabel}>
                      <span
                        className={`${styles.stationDot} ${
                          station.online ? styles.on : styles.off
                        }`}
                      />
                      {station.label}
                    </span>
                    {meta ? (
                      <span className={`${styles.statusTag} ${styles[meta.tone]}`}>
                        {meta.label}
                      </span>
                    ) : (
                      <span className={`${styles.statusTag} ${styles.muted}`}>—</span>
                    )}
                  </div>

                  <span className={styles.stationIp}>{station.ip}</span>

                  <div className={styles.stationMeta}>
                    <span className={styles.metaItem}>
                      {station.online ? 'Online' : 'Offline'}
                    </span>
                    <span className={styles.metaSep}>·</span>
                    <span className={styles.metaItem}>
                      {station.numeroOP ? `OP ${station.numeroOP}` : 'Sem OP'}
                    </span>
                    {station.emergencia && (
                      <span className={`${styles.flag} ${styles.danger}`}>Emergência</span>
                    )}
                    {station.manual && !station.emergencia && (
                      <span className={`${styles.flag} ${styles.warn}`}>Manual</span>
                    )}
                  </div>
                </li>
              );
            })}
          </ul>
        </section>
      </main>
    </AppTemplate>
  );
}
