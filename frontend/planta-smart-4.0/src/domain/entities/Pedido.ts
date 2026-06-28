import { Bloco } from '@entities/Bloco';
import { StatusPedido } from '@enums/StatusPedido';
import { TipoPedido } from '@enums/TipoPedido';
import { CorTampa } from '@enums/CorTampa';
import { Expedicao } from './Expedicao';

export interface Pedido {
  id: number | null;
  ordemDeProducao: number;
  blocos: Bloco[];
  status: StatusPedido;
  tipo: TipoPedido;
  corTampa: CorTampa;
  expedicao: Expedicao | null;
  registroCriacao: string | null;
  registroEntradaExpedicao: string | null;
  registroSaidaExpedicao: string | null;
  registroEntradaEstoque: string | null;
}
