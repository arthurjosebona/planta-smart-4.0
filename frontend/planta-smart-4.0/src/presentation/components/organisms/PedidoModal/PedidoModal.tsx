import { PedidoCardHeader } from '@components/molecules/PedidoCardHeader/PedidoCardHeader';
import { PedidoCardFields } from '@components/molecules/PedidoCardFields/PedidoCardFields';
import { OrderViewer } from '@components/organisms/OrderViewer/OrderViewer';
import { Pedido } from '@entities/Pedido';
import { pedidoToStoreModel } from '@utils/pedidoToStoreModel';
import styles from '@components/organisms/PedidoModal/pedidoModal.module.css';
import { ActionButton } from '@components/atoms/ActionButton/ActionButton';
import { StatusPedido } from '@enums/StatusPedido';

interface PedidoModalProps {
  pedido: Pedido;
  iniciarProducao: (id: number) => void;
  deletarPedido: (id: number) => void;
  onClose: () => void;
  conectado: boolean;
}

export function PedidoModal({ pedido, iniciarProducao, enviarParaProducao, deletarPedido, onClose, conectado }: PedidoModalProps) {
  return (
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
          <div className={styles.details}>
            <PedidoCardHeader
              id={pedido.id!}
              ordemDeProducao={pedido.ordemDeProducao}
              status={pedido.status}
              iniciarProducao={iniciarProducao}
              conectado={conectado}
            />

            <hr className={styles.divider} />

            <PedidoCardFields
              tipo={pedido.tipo}
              corTampa={pedido.corTampa}
              blocos={pedido.blocos}
              expedicaoId={pedido.expedicao?.id ?? null}
              registroCriacao={pedido.registroCriacao!}
              registroEntradaExpedicao={pedido.registroEntradaExpedicao}
              registroSaidaExpedicao={pedido.registroSaidaExpedicao}
            />
            { pedido.status == StatusPedido.Pendente &&
              <>
                <hr className={styles.divider} />
                <div className={styles.updateDeleteButtons}>
                  <ActionButton label="Deletar" onClick={() => deletarPedido(pedido.id!)} />
                </div>
              </>
            }
          </div>

          <div className={styles.viewer}>
            <OrderViewer state={pedidoToStoreModel(pedido)} />
          </div>
        </div>
      </div>
    </div>
  );
}
