import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { Expedicao } from '@entities/Expedicao';
import { expedicaoService } from '@config/diContainer';
import { HttpError } from '@error/HttpError';

interface ExpedicaoContextValue {
  expedicao: Expedicao[];
  editMode: boolean;
  selectedId: number | null;
  opInput: string;
  loading: boolean;
  erro: string | null;
  fetchExpedicao: () => Promise<void>;
  enterEditMode: () => void;
  cancelEditMode: () => void;
  selectSlot: (id: number) => void;
  changeOpInput: (value: string) => void;
  saveExpedicao: () => Promise<void>;
  dismissErro: () => void;
}

const ExpedicaoContext = createContext<ExpedicaoContextValue | null>(null);

export function ExpedicaoProvider({ children }: { children: ReactNode }) {
  const [expedicao, setExpedicao] = useState<Expedicao[]>([]);
  const [snapshot, setSnapshot] = useState<Expedicao[]>([]);
  const [editMode, setEditMode] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [opInput, setOpInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    fetchExpedicao();
  }, []);

  async function fetchExpedicao() {
    setLoading(true);
    setErro(null);
    try {
      const data = await expedicaoService.findAll();
      setExpedicao(data);
    } catch {
      setErro('Erro ao carregar expedição.');
    } finally {
      setLoading(false);
    }
  }

  function enterEditMode() {
    setSnapshot(expedicao);
    setEditMode(true);
    setSelectedId(null);
    setOpInput('');
  }

  function cancelEditMode() {
    setExpedicao(snapshot);
    setSnapshot([]);
    setEditMode(false);
    setSelectedId(null);
    setOpInput('');
  }

  function selectSlot(id: number) {
    // aplica o input pendente no slot anterior antes de trocar
    const expedicaoAtualizada =
      selectedId !== null
        ? expedicao.map((slot) =>
            slot.id === selectedId ? { ...slot, ordemDeProducaoAtual: Number(opInput) } : slot
          )
        : expedicao;

    const slotSelecionado = expedicaoAtualizada.find((slot) => slot.id === id);

    setExpedicao(expedicaoAtualizada);
    setSelectedId(id);
    setOpInput(slotSelecionado?.ordemDeProducaoAtual?.toString() ?? '');
  }

  function changeOpInput(value: string) {
    setOpInput(value);
  }

  async function saveExpedicao() {
    // aplica o input pendente do slot ainda aberto antes de salvar
    const expedicaoFinal =
      selectedId !== null
        ? expedicao.map((slot) =>
            slot.id === selectedId ? { ...slot, ordemDeProducaoAtual: Number(opInput) } : slot
          )
        : expedicao;

    setExpedicao(expedicaoFinal);
    setLoading(true);
    setErro(null);
    try {
      await expedicaoService.updateAll(expedicaoFinal);
      setEditMode(false);
      setSnapshot([]);
      setSelectedId(null);
      setOpInput('');
    } catch (error: unknown) {
      setErro(error instanceof HttpError ? error.message : 'Erro desconhecido');
    } finally {
      setLoading(false);
    }
  }

  function dismissErro() {
    setErro(null);
  }

  return (
    <ExpedicaoContext.Provider
      value={{
        expedicao,
        editMode,
        selectedId,
        opInput,
        loading,
        erro,
        fetchExpedicao,
        enterEditMode,
        cancelEditMode,
        selectSlot,
        changeOpInput,
        saveExpedicao,
        dismissErro,
      }}
    >
      {children}
    </ExpedicaoContext.Provider>
  );
}

export function useExpedicaoContext() {
  const ctx = useContext(ExpedicaoContext);
  if (!ctx) {
    throw new Error('useExpedicaoContext deve ser usado dentro de um ExpedicaoProvider');
  }
  return ctx;
}
