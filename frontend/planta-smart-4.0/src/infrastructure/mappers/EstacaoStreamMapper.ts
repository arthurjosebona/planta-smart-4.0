import { EstoqueStreamDTO } from '@dtos/response/EstoqueStreamDTO';
import { ExpedicaoStreamDTO } from '@dtos/response/ExpedicaoStreamDTO';
import { ProcessoMontagemStreamDTO } from '@dtos/response/ProcessoMontagemStreamDTO';
import { EstoqueStream, ExpedicaoStream, ProcessoMontagemStream } from '@entities/EstacaoStream';
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
