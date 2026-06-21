import { useEstoqueContext } from '@contexts/EstoqueContext';
import { useExpedicaoContext } from '@contexts/ExpedicaoContext';

export function useEstacoesViewModel() {
  const estoque = useEstoqueContext();
  const expedicao = useExpedicaoContext();

  function dismissErro() {
    estoque.dismissErro();
    expedicao.dismissErro();
  }

  return {
    estoque,
    expedicao,
    erro: estoque.erro ?? expedicao.erro,
    dismissErro,
  };
}
