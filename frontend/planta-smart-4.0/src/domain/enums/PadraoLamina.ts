export enum PadraoLamina {
  Nenhum = 'nenhum',
  Casa = 'casa',
  Navio = 'navio',
  Estrela = 'estrela',
}

export const PadraoLaminaToInt: Record<PadraoLamina, number> = {
  [PadraoLamina.Nenhum]: 0,
  [PadraoLamina.Casa]: 1,
  [PadraoLamina.Navio]: 2,
  [PadraoLamina.Estrela]: 3,
};

export const PadraoLaminaStringToEnum: Record<string, PadraoLamina> = {
  NENHUM: PadraoLamina.Nenhum,
  CASA: PadraoLamina.Casa,
  NAVIO: PadraoLamina.Navio,
  ESTRELA: PadraoLamina.Estrela,
};
