import { Bloco } from '@entities/Bloco';
import { Lamina } from '@entities/Lamina';
import { Pedido } from '@entities/Pedido';
import { AndarBloco } from '@enums/AndarBloco';
import { CorTampa } from '@enums/CorTampa';
import { PosicaoLamina } from '@enums/PosicaoLamina';
import { StatusPedido } from '@enums/StatusPedido';
import { IntToTipoPedido } from '@enums/TipoPedido';
import { IPedidoRepository } from '@repositories/IPedidoRepository';
import { ConfigBloco } from '@valueObjects/ConfigBloco';

interface PedidoCreateInput {
  ordemDeProducao: number;
  numBlocos: 1 | 2 | 3;
  corTampa: CorTampa;
  blocos: [ConfigBloco, ConfigBloco, ConfigBloco];
}

const ANDARES = [AndarBloco.Primeiro, AndarBloco.Segundo, AndarBloco.Terceiro] as const;

export class PedidoService {
  private readonly repository;

  constructor(repository: IPedidoRepository) {
    this.repository = repository;
  }

  async create(input: PedidoCreateInput): Promise<Pedido> {
    const blocos: Bloco[] = input.blocos.slice(0, input.numBlocos).map(
      (config, i): Bloco => ({
        id: null,
        cor: config.cor,
        andar: ANDARES[i],
        laminas: Object.entries(config.laminas).map(
          ([posicao, lamina]): Lamina => ({
            id: null,
            posicao: posicao as PosicaoLamina,
            cor: lamina.cor!,
            padrao: lamina.padrao!,
          })
        ),
      })
    );

    const pedido: Pedido = {
      ordemDeProducao: input.ordemDeProducao,
      tipo: IntToTipoPedido[input.numBlocos],
      corTampa: input.corTampa,
      blocos: blocos,
      id: null,
      status: StatusPedido.Pendente,
      registroCriacao: null,
      registroEntradaExpedicao: null,
      registroSaidaExpedicao: null,
    };
    return this.repository.createPedido(pedido);
  }
}
