export enum CorBloco {
  Preto = 'preto',
  Vermelho = 'vermelho',
  Azul = 'azul',
}

export const CorBlocoToInt: Record<CorBloco, number> = {
  [CorBloco.Preto]: 1,
  [CorBloco.Vermelho]: 2,
  [CorBloco.Azul]: 3,
};
