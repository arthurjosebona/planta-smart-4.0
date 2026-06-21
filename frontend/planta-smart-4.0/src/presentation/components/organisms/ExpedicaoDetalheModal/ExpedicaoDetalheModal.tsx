import { useEffect, useState } from 'react';
import { Pedido } from '@entities/Pedido';
import { pedidoService } from '@config/diContainer';
import { HttpError } from '@error/HttpError';
import { PedidoCard } from '@components/organisms/PedidoCard/PedidoCard';
import { PedidoModal } from '@components/organisms/PedidoModal/PedidoModal';
import { OrderViewer } from '@components/organisms/OrderViewer/OrderViewer';
import { pedidoToStoreModel } from '@utils/pedidoToStoreModel';
import clsx from 'clsx';
import styles from '@components/organisms/ExpedicaoDetalheModal/expedicaoDetalheModal.module.css';

interface ExpedicaoDetalheModalProps {
  expedicaoId: number;
  posicaoFisica: number;
  /** OP atualmente na expedição. 0 (ou nulo) significa vazio: nenhum modelo é exibido. */
  op: number | null;
  iniciarProducao: (id: number) => void;
  onClose: () => void;
}

export function ExpedicaoDetalheModal({
  expedicaoId,
  posicaoFisica,
  op,
  iniciarProducao,
  onClose,
}: ExpedicaoDetalheModalProps) {
  const [pedidos, setPedidos] = useState<Pedido[]>([]);
  const [pedidoNaExpedicao, setPedidoNaExpedicao] = useState<Pedido | null>(null);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [pedidoSelecionadoId, setPedidoSelecionadoId] = useState<number | null>(null);

  const temOp = op !== null && op !== 0;
  const pedidoSelecionado = pedidos.find((p) => p.id === pedidoSelecionadoId) ?? null;

  async function deletarPedido(id: number) {
    try {
      await pedidoService.delete(id);
      setPedidos((atual) => atual.filter((p) => p.id !== id));
      setPedidoSelecionadoId(null);
    } catch (error: unknown) {
      setErro(error instanceof HttpError ? error.message : 'Erro ao deletar pedido.');
    }
  }

  useEffect(() => {
    let ativo = true;

    async function carregar() {
      setLoading(true);
      setErro(null);
      try {
        const lista = await pedidoService.findByExpedicao(expedicaoId);
        // Só busca o pedido na expedição quando há OP (≠ 0).
        const naExpedicao = temOp ? await pedidoService.findByOrdemDeProducao(op!) : null;
        if (!ativo) return;
        setPedidos(lista);
        setPedidoNaExpedicao(naExpedicao);
      } catch (error: unknown) {
        if (!ativo) return;
        setErro(error instanceof HttpError ? error.message : 'Erro ao carregar pedidos da expedição.');
      } finally {
        if (ativo) setLoading(false);
      }
    }

    carregar();
    return () => {
      ativo = false;
    };
  }, [expedicaoId, op, temOp]);

  return (
    <>
      <div className={styles.overlay} onClick={onClose}>
        <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <button
          type="button"
          className={styles.closeBtn}
          onClick={onClose}
          aria-label="Fechar"
        >
          ✕
        </button>

        <div className={styles.content}>
          <div className={clsx(styles.lista, !temOp && styles['lista--full'])}>
            <h2 className={styles.titulo}>Expedição #{posicaoFisica}</h2>
            <hr className={styles.divider} />

            {loading ? (
              <p className={styles.stateMsg}>Carregando…</p>
            ) : erro ? (
              <p className={styles.stateMsg}>{erro}</p>
            ) : pedidos.length === 0 ? (
              <p className={styles.stateMsg}>Nenhum pedido nesta expedição.</p>
            ) : (
              <div className={styles.cards}>
                {pedidos.map((p) => (
                  <PedidoCard
                    key={p.id}
                    pedido={p}
                    onClick={() => setPedidoSelecionadoId(p.id)}
                  />
                ))}
              </div>
            )}
          </div>

          {temOp && (
            <div className={styles.viewer}>
              {pedidoNaExpedicao ? (
                <OrderViewer state={pedidoToStoreModel(pedidoNaExpedicao)} />
              ) : (
                !loading && <p className={styles.stateMsg}>Pedido da OP {op} não encontrado.</p>
              )}
            </div>
          )}
          </div>
        </div>
      </div>

      {pedidoSelecionado && (
        <PedidoModal
          pedido={pedidoSelecionado}
          iniciarProducao={iniciarProducao}
          deletarPedido={deletarPedido}
          onClose={() => setPedidoSelecionadoId(null)}
        />
      )}
    </>
  );
}
