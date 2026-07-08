import { ClpStatusChip } from '@components/atoms/ClpStatusChip/ClpStatusChip';
import { ClpFlagGroup } from '@components/molecules/ClpFlagGroup/ClpFlagGroup';
import { ClpValueField } from '@components/atoms/ClpValueField/ClpValueField';
import styles from './clpStationCard.module.css';
import { EstoqueStream } from '@entities/stream/EstoqueStream';
import { ExpedicaoStream } from '@entities/stream/ExpedicaoStream';
import { ProcessoMontagemStream } from '@entities/stream/ProcessoMontagemStream';

interface ClpStationCardProps {
  label: string;
  color: string;
  data: EstoqueStream | ExpedicaoStream | ProcessoMontagemStream | null;
  online: boolean;
}

export function ClpStationCard({ label, color, data, online }: ClpStationCardProps) {
  return (
    <div className={styles.card} style={{ '--station-color': color } as React.CSSProperties}>
      <div className={styles.accent} />

      <div className={styles.header}>
        <span className={styles.name}>{label}</span>
        {data ? (
          <ClpStatusChip status={data.status} />
        ) : (
          <span className={styles.waiting}>Aguardando stream…</span>
        )}
      </div>

      <div className={`${styles.pingBadge} ${online ? styles.pingOnline : styles.pingOffline}`}>
        <span className={styles.pingDot} aria-hidden="true" />
        <span>{online ? 'Online' : 'Offline'}</span>
      </div>

      {data ? (
        <div className={styles.body}>
          <div className={styles.op}>
            <span className={styles.opLabel}>OP</span>
            <span className={styles.opValue}>{data.numeroOP}</span>
          </div>

          <ClpFlagGroup
            title="Estado"
            flags={[
              { label: 'ocupado', value: data.ocupado },
              { label: 'aguardando', value: data.aguardando },
              { label: 'manual', value: data.manual },
              { label: 'emergência', value: data.emergencia },
            ]}
          />

          <ClpFlagGroup
            title="Ações"
            flags={[
              { label: 'recebidoOp', value: data.recebidoOp },
              { label: 'startOP', value: data.startOP },
              { label: 'finishOP', value: data.finishOP },
              { label: 'cancelOP', value: data.cancelOP },
            ]}
          />

          {data.estacao === 'estoque' && <EstoqueFields data={data} />}
          {(data.estacao === 'processo' || data.estacao === 'montagem') && (
            <ProcessoFields data={data} />
          )}
          {data.estacao === 'expedicao' && <ExpedicaoFields data={data} />}
        </div>
      ) : (
        <div className={styles.empty}>
          <span className={styles.pulse} />
        </div>
      )}
    </div>
  );
}

function EstoqueFields({ data }: { data: EstoqueStream }) {
  return (
    <>
      <ClpFlagGroup
        title="Ações"
        flags={[
          { label: 'iniciarPedido', value: data.iniciarPedido },
          { label: 'pedirPosicaoEst', value: data.pedirPosicaoEst },
          { label: 'adicionarEstoque', value: data.adicionarEstoque },
          { label: 'removerEstoque', value: data.removerEstoque },
          { label: 'retornoEstCheio', value: data.retornoEstoqueCheio },
          { label: 'recebidoEstoque', value: data.recebidoEstoque },
          { label: 'iniciarGuardarEst', value: data.iniciarGuardarEst },
          { label: 'pedidoEmCurso', value: data.pedidoEmCurso },
        ]}
      />
      <div className={styles.values}>
        <ClpValueField label="posicaoEstoque" value={data.posicaoEstoque} />
        <ClpValueField label="posicaoGuardarEst" value={data.posicaoGuardarEst} />
        <ClpValueField label="corGuardarEstoque" value={data.corGuardarEstoque} />
        <ClpValueField label="posicoesOcupadas" value={data.posicoesOcupadas} />
        <ClpValueField label="statusEstoque" value={data.statusEstoque} />
        <ClpValueField label="statusProcesso" value={data.statusProcesso} />
        <ClpValueField label="statusMontagem" value={data.statusMontagem} />
        <ClpValueField label="statusExpedicao" value={data.statusExpedicao} />
        <ClpValueField label="statusProducao" value={data.statusProducao} />
        <ClpValueField label="registroInicioPedido" value={formatTimestampUtc(data.registroInicioPedido)} />
      </div>
    </>
  );
}

// O backend envia um LocalDateTime (sem offset) sempre em UTC — sem o sufixo
// 'Z' o Date interpretaria a string no fuso local do navegador (ver useTempoDecorrido).
function formatTimestampUtc(registro: string | null | undefined): string {
  if (!registro) return '—';

  const registroUtc = /[zZ]|[+-]\d{2}:\d{2}$/.test(registro) ? registro : `${registro}Z`;
  const data = new Date(registroUtc);
  if (Number.isNaN(data.getTime())) return '—';

  return data.toLocaleString();
}

function ProcessoFields({ data }: { data: ProcessoMontagemStream }) {
  return (
    <>
      <div className={styles.values}>
        <ClpValueField label="statusBancada" value={data.statusBancada} />
      </div>
    </>
  );
}

function ExpedicaoFields({ data }: { data: ExpedicaoStream }) {
  return (
    <>
      <ClpFlagGroup
        title="Ações"
        flags={[
          { label: 'pedirPosicaoExp', value: data.pedirPosicaoExp },
          { label: 'adicionarExpedicao', value: data.adicionarExpedicao },
          { label: 'removerExpedicao', value: data.removerExpedicao },
          { label: 'iniciarGuardarExp', value: data.iniciarGuardarExp },
          { label: 'recebidoExpedicao', value: data.recebidoExpedicao },
        ]}
      />
      <div className={styles.values}>
        <ClpValueField label="posicaoGuardarExp" value={data.posicaoGuardarExp} />
        <ClpValueField label="posGuardadoExp" value={data.posicaoGuardadoExpedicao} />
        <ClpValueField label="posRemovidoExp" value={data.posicaoRemovidoExpedicao} />
        <ClpValueField label="opGuardadoExp" value={data.opGuardadoExpedicao} />
        <ClpValueField label="orderExpedicao" value={data.orderExpedicao} />
        <ClpValueField label="statusExpedicao" value={data.statusExpedicao} />
      </div>
    </>
  );
}
