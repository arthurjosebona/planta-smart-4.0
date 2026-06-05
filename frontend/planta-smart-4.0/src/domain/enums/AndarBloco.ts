export enum AndarBloco {
  Primeiro,
  Segundo,
  Terceiro,
}

export const AndarBlocoToInt: Record<AndarBloco, number> = {
  [AndarBloco.Primeiro]: 1,
  [AndarBloco.Segundo]: 2,
  [AndarBloco.Terceiro]: 3,
};
