import { Estoque } from '@entities/Estoque';
import { Expedicao } from '@entities/Expedicao';

export interface StoreModel {
  estoque: Estoque[];
  expedicao: Expedicao[];
  expedicaoSnapshot: Expedicao[];
  editMode: boolean;
  selectedIds: number[];
  loading: boolean;
  erro: string | null;
  expedicaoEditMode: boolean;
  selectedExpedicaoId: number | null;
  opInput: string;
}

export const StoreModelInitial: StoreModel = {
  estoque: [],
  expedicao: [],
  expedicaoSnapshot: [],
  editMode: false,
  selectedIds: [],
  loading: false,
  erro: null,
  expedicaoEditMode: false,
  selectedExpedicaoId: null,
  opInput: '',
};