import { EstoqueStreamDTO } from '@dtos/response/stream/EstoqueStreamDTO';
import { ExpedicaoStreamDTO } from '@dtos/response/stream/ExpedicaoStreamDTO';
import { ProcessoMontagemStreamDTO } from '@dtos/response/stream/ProcessoMontagemStreamDTO';
import { EstoqueStream } from '@entities/stream/EstoqueStream';
import { ExpedicaoStream } from '@entities/stream/ExpedicaoStream';
import { ProcessoMontagemStream } from '@entities/stream/ProcessoMontagemStream';
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

  mapProcessoMontagemByDTO(dto: ProcessoMontagemStreamDTO): ProcessoMontagemStream {
    const { status, ...rest } = dto;
    return { ...rest, status: StatusEstacao.fromString(status) };
  },
};
