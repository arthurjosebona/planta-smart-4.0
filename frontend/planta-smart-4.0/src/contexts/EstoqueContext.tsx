import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { Estoque } from '@entities/Estoque';
import { CorEstoque } from '@enums/CorEstoque';
import { estoqueService } from '@config/diContainer';
import { HttpError } from '@error/HttpError';

interface EstoqueContextValue {
  estoque: Estoque[];
  editMode: boolean;
  selectedIds: number[];
  loading: boolean;
  erro: string | null;
  fetchEstoque: () => Promise<void>;
  enterEditMode: () => void;
  cancelEditMode: () => void;
  toggleBlocoSelection: (id: number) => void;
  changeBlockColor: (cor: CorEstoque) => void;
  cleanEstoque: () => void;
  saveEstoque: () => Promise<void>;
  dismissErro: () => void;
}

const EstoqueContext = createContext<EstoqueContextValue | null>(null);

export function EstoqueProvider({ children }: { children: ReactNode }) {
  const [estoque, setEstoque] = useState<Estoque[]>([]);
  const [editMode, setEditMode] = useState(false);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    fetchEstoque();
  }, []);

  async function fetchEstoque() {
    setLoading(true);
    setErro(null);
    try {
      const data = await estoqueService.findAll();
      setEstoque(data);
    } catch {
      setErro('Erro ao carregar estoque.');
    } finally {
      setLoading(false);
    }
  }

  function enterEditMode() {
    setEditMode(true);
    setSelectedIds([]);
  }

  function cancelEditMode() {
    setEditMode(false);
    setSelectedIds([]);
  }

  function toggleBlocoSelection(id: number) {
    setSelectedIds((ids) => (ids.includes(id) ? ids.filter((i) => i !== id) : [...ids, id]));
  }

  function changeBlockColor(cor: CorEstoque) {
    setEstoque((blocos) =>
      blocos.map((bloco) => (selectedIds.includes(bloco.id) ? { ...bloco, cor } : bloco))
    );
    setSelectedIds([]);
  }

  function cleanEstoque() {
    setEstoque((blocos) =>
      blocos.map((bloco) =>
        selectedIds.includes(bloco.id) ? { ...bloco, cor: CorEstoque.Vazio } : bloco
      )
    );
    setSelectedIds([]);
  }

  async function saveEstoque() {
    setLoading(true);
    setErro(null);
    try {
      await estoqueService.updateAll(estoque);
      setEditMode(false);
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
    <EstoqueContext.Provider
      value={{
        estoque,
        editMode,
        selectedIds,
        loading,
        erro,
        fetchEstoque,
        enterEditMode,
        cancelEditMode,
        toggleBlocoSelection,
        changeBlockColor,
        cleanEstoque,
        saveEstoque,
        dismissErro,
      }}
    >
      {children}
    </EstoqueContext.Provider>
  );
}

export function useEstoqueContext() {
  const ctx = useContext(EstoqueContext);
  if (!ctx) {
    throw new Error('useEstoqueContext deve ser usado dentro de um EstoqueProvider');
  }
  return ctx;
}
