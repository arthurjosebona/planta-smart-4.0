import { EstoqueStreamDTO } from '@dtos/response/stream/EstoqueStreamDTO';
import { ExpedicaoStreamDTO } from '@dtos/response/stream/ExpedicaoStreamDTO';
import { MontagemStreamDTO } from '@dtos/response/stream/MontagemStreamDTO';
import { ProcessoStreamDTO } from '@dtos/response/stream/ProcessoStreamDTO';
import { EstoqueStream } from '@entities/stream/EstoqueStream';
import { ExpedicaoStream } from '@entities/stream/ExpedicaoStream';
import { MontagemStream } from '@entities/stream/MontagemStream';
import { ProcessoStream } from '@entities/stream/ProcessoStream';
import { StatusEstacao } from '@enums/StatusEstacao';

export const EstacaoStreamMapper = {
  mapEstoqueByDTO(dto: EstoqueStreamDTO): EstoqueStream {
    const { status, ...rest } = dto;
    return { ...rest, status: StatusEstacao.fromString(status) };
  },

  mapExpedicaoByDTO(dto: ExpedicaoStreamDTO): ExpedicaoStream {
    const { status, ...rest } = dto;
    return { ...rest, status: StatusEstacao.fromString(status) };
  },

  mapProcessoByDTO(dto: ProcessoStreamDTO): ProcessoStream {
    const { status, ...rest } = dto;
    return { ...rest, status: StatusEstacao.fromString(status) };
  },

  mapMontagemByDTO(dto: MontagemStreamDTO): MontagemStream {
    const { status, ...rest } = dto;
    return { ...rest, status: StatusEstacao.fromString(status) };
  }
};
