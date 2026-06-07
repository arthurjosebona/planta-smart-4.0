import React from 'react';
import { FieldDisplay } from '@components/atoms/FieldDisplay';
import { CorTampaDot } from '@components/atoms/CorTampaDot';
import { CorTampa } from '@enums/CorTampa';
import { TipoPedido } from '@enums/TipoPedido';

interface PedidoCardFieldsProps {
  tipo: TipoPedido;
  corTampa: CorTampa;
  numBlocos: number;
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

export const PedidoCardFields: React.FC<PedidoCardFieldsProps> = ({
  tipo,
  corTampa,
  numBlocos,
  expedicaoId,
  registroCriacao,
  registroEntradaExpedicao,
  registroSaidaExpedicao,
}) => {
  return (
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
        {numBlocos}
      </FieldDisplay>

      <FieldDisplay
        label="Expedição"
        empty={expedicaoId === null}
      >
        {expedicaoId ?? '—'}
      </FieldDisplay>

      <FieldDisplay label="Registro Criação">
        {fmtData(registroCriacao)}
      </FieldDisplay>

      <FieldDisplay
        label="Entrada Expedição"
        empty={registroEntradaExpedicao === null}
      >
        {fmtData(registroEntradaExpedicao)}
      </FieldDisplay>

      <FieldDisplay
        label="Saída Expedição"
        empty={registroSaidaExpedicao === null}
      >
        {fmtData(registroSaidaExpedicao)}
      </FieldDisplay>
    </div>
  );
};