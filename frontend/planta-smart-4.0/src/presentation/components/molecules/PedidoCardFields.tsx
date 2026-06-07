import React from 'react';
import { FieldDisplay } from '@components/atoms/FieldDisplay';
import { CorTampaDot } from '@components/atoms/CorTampaDot';
import { CorTampa } from '@enums/CorTampa';
import { TipoPedido } from '@enums/TipoPedido';
import { Bloco } from '@entities/Bloco';
import { BlocoResumo } from '@components/atoms/BlocoResumo';

interface PedidoCardFieldsProps {
  tipo: TipoPedido;
  corTampa: CorTampa;
  blocos: Bloco[];
  expedicaoId: number | null;
  registroCriacao: string;
  registroEntradaExpedicao: string | null;
  registroSaidaExpedicao: string | null;
}

function fmtData(iso: string | null): string {
  if (!iso) return '—';
  const d = new Date(iso);
  const dd = String(d.getDate()).padStart(2, '0');
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const yyyy = d.getFullYear();
  const hh = String(d.getHours()).padStart(2, '0');
  const mi = String(d.getMinutes()).padStart(2, '0');
  return `${dd}/${mm}/${yyyy} ${hh}:${mi}`;
}

function capitalize(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export function PedidoCardFields({
  tipo,
  corTampa,
  blocos,
  expedicaoId,
  registroCriacao,
  registroEntradaExpedicao,
  registroSaidaExpedicao,
}: PedidoCardFieldsProps) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
      {/* campos gerais do pedido */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))',
          gap: '10px 16px',
        }}
      >
        <FieldDisplay label="Tipo" highlight>
          {capitalize(TipoPedido[tipo])}
        </FieldDisplay>

        <FieldDisplay label="Cor da Tampa">
          <CorTampaDot cor={corTampa} />
        </FieldDisplay>

        <FieldDisplay label="Blocos" highlight>
          {blocos.length}
        </FieldDisplay>

        <FieldDisplay label="Expedição" empty={expedicaoId === null}>
          {expedicaoId ?? '—'}
        </FieldDisplay>

        <FieldDisplay label="Registro Criação">{fmtData(registroCriacao)}</FieldDisplay>

        <FieldDisplay label="Entrada Expedição" empty={registroEntradaExpedicao === null}>
          {fmtData(registroEntradaExpedicao)}
        </FieldDisplay>

        <FieldDisplay label="Saída Expedição" empty={registroSaidaExpedicao === null}>
          {fmtData(registroSaidaExpedicao)}
        </FieldDisplay>
      </div>

      {/* resumo de cada bloco */}
      {blocos.length > 0 && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          {blocos.map((bloco, i) => (
            <BlocoResumo key={bloco.id ?? i} bloco={bloco} index={i} />
          ))}
        </div>
      )}
    </div>
  );
};
