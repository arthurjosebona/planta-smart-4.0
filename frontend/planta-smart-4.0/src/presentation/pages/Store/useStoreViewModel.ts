import { useState } from 'react';
import { StoreModel, StoreModelInitial } from './StoreModel';
import { CorTampa } from '../../../domain/enums/CorTampa';
import { CorBloco } from '../../../domain/enums/CorBloco';
import { PosicaoLamina } from '../../../domain/enums/PosicaoLamina';
import { CorLamina } from '../../../domain/enums/CorLamina';
import { PadraoLamina } from '../../../domain/enums/PadraoLamina';
import { ConfigBloco } from '../../../domain/entities/ConfigBloco';

export function useStoreViewModel() {
  const [model, setModel] = useState<StoreModel>(StoreModelInitial);

  function setNumBlocos(n: 1 | 2 | 3) {
    setModel((s) => ({ ...s, numBlocos: n }));
  }

  function setCorTampa(cor: CorTampa) {
    setModel((s) => ({ ...s, corTampa: cor }));
  }

  function setBlocoField(idx: number, updates: Partial<ConfigBloco>) {
    const blocos = [...model.blocos] as typeof model.blocos;
    blocos[idx] = { ...blocos[idx], ...updates };
    setModel((s) => ({ ...s, blocos }));
  }

  function setBlocoColor(idx: number, cor: CorBloco) {
    setBlocoField(idx, { cor });
  }

  function setLaminaCor(idx: number, posicao: PosicaoLamina, cor: CorLamina | null) {
    const blocos = [...model.blocos] as typeof model.blocos;
    const laminas = { ...blocos[idx].laminas };
    laminas[posicao] = { cor, padrao: cor === null ? null : laminas[posicao].padrao };
    blocos[idx] = { ...blocos[idx], laminas };
    setModel((s) => ({ ...s, blocos }));
  }

  function setLaminaPadrao(idx: number, posicao: PosicaoLamina, padrao: PadraoLamina | null) {
    const blocos = [...model.blocos] as typeof model.blocos;
    const laminas = { ...blocos[idx].laminas };
    laminas[posicao] = { ...laminas[posicao], padrao };
    blocos[idx] = { ...blocos[idx], laminas };
    setModel((s) => ({ ...s, blocos }));
  }

  return {
    model,
    setNumBlocos,
    setCorTampa,
    setBlocoField,
    setBlocoColor,
    setLaminaCor,
    setLaminaPadrao,
  };
}
