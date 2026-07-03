export interface MontagemStreamDTO {
    estacao: 'montagem';
    status: string;
    numeroOP: number;
    ocupado: boolean;
    aguardando: boolean;
    manual: boolean;
    emergencia: boolean;
    recebidoOp: boolean;
    startOP: boolean;
    finishOP: boolean;
    cancelOP: boolean;
    statusBancada: number;
    supervisorioEstoque: string;
    supervisorioProcesso: string;
    supervisorioMontagem: string;
    supervisorioExpedicao: string;
  }   