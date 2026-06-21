export enum StatusEstacao {
  Parado = 'parado',
  Ocupado = 'ocupado',
  Aguardando = 'aguardando',
  Manual = 'manual',
  Emergencia = 'emergencia',
}

const statusEstacaoMap: Record<number, StatusEstacao> = {
  0: StatusEstacao.Parado,
  1: StatusEstacao.Ocupado,
  2: StatusEstacao.Aguardando,
  3: StatusEstacao.Manual,
  4: StatusEstacao.Emergencia,
};

const statusEstacaoStringMap: Record<string, StatusEstacao> = {
  PARADO: StatusEstacao.Parado,
  OCUPADO: StatusEstacao.Ocupado,
  AGUARDANDO: StatusEstacao.Aguardando,
  MANUAL: StatusEstacao.Manual,
  EMERGENCIA: StatusEstacao.Emergencia,
};

export namespace StatusEstacao {
  export function fromValue(value: number): StatusEstacao {
    const status = statusEstacaoMap[value];
    if (status === undefined) {
      throw new Error(`StatusEstacao inválido recebido do backend: ${value}`);
    }
    return status;
  }

  export function fromString(value: string): StatusEstacao {
    const status = statusEstacaoStringMap[value.toUpperCase()];
    if (status === undefined) {
      throw new Error(`StatusEstacao inválido recebido do backend: "${value}"`);
    }
    return status;
  }
}