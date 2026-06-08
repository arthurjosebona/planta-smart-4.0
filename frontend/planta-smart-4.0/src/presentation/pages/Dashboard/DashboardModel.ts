import { Estoque } from '@entities/Estoque';
import { Expedicao } from '@entities/Expedicao';

export interface StoreModel {
  estoque: Estoque[];
  expedicao: Expedicao[];
  editMode: boolean;
  selectedIds: number[];
  loading: boolean;
  erro: string | null;
}

export const StoreModelInitial: StoreModel = {
  estoque: [],
  expedicao: [],
  editMode: false,
  selectedIds: [],
  loading: false,
  erro: null,
};
