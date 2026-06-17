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

function montarBlocos(input: PedidoCreateInput): Bloco[] {
  return input.blocos.slice(0, input.numBlocos).map(
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
}

export class PedidoService {
  private readonly repository;

  constructor(repository: IPedidoRepository) {
    this.repository = repository;
  }

  async create(input: PedidoCreateInput): Promise<Pedido> {
    const blocos = montarBlocos(input);

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
      expedicao: null,
    };
    return this.repository.createPedido(pedido);
  }

  async findAll(): Promise<Pedido[]> {
    return await this.repository.findAll();
  }

  async findById(id: number): Promise<Pedido> {
    return await this.repository.findById(id);
  }

  async iniciarProducao(id: number): Promise<Pedido> {
    return await this.repository.iniciarProducao(id);
  }

  async update(id: number, input: PedidoCreateInput): Promise<Pedido> {
    const blocos = montarBlocos(input);

    const pedido: Pedido = {
      ordemDeProducao: input.ordemDeProducao,
      tipo: IntToTipoPedido[input.numBlocos],
      corTampa: input.corTampa,
      blocos: blocos,
      id: id,
      status: StatusPedido.Pendente, // ignorado pelo backend no update, mas mantém o tipo Pedido coerente
      registroCriacao: null,
      registroEntradaExpedicao: null,
      registroSaidaExpedicao: null,
      expedicao: null,
    };
    return this.repository.update(id, pedido);
  }

  async delete(id: number): Promise<void> {
    return this.repository.delete(id);
  }
}