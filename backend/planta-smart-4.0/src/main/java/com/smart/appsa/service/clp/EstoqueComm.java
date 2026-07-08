package com.smart.appsa.service.clp;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.clp.EstoqueInfoClp;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.service.EstoqueService;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.clp.reader.PlcDataObserver;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Handler da estação ESTOQUE (CLP 1 / DB9).
//
// <p>Registrado como {@link PlcDataObserver} no {@code PlcReaderTask} da estação,
// recebe a cada ciclo o bloco bruto lido do CLP, atualiza o {@link EstoqueInfoClp}
// e aplica, em sequência, as regras de negócio do estoque: confirmação de início
// de pedido, sincronização das flags de operação, adição/remoção de blocos no
// magazine e atendimento ao pedido de posição livre para guardar.
//
// <p>Toda escrita no CLP é condicionada a {@code !appStateConfig.isReadOnly()}:
// em modo somente-leitura a aplicação observa, mas nunca escreve de volta.
@Slf4j
@Service
@AllArgsConstructor
public class EstoqueComm implements PlcDataObserver {
    // Data Block da estação ESTOQUE.
    private static final int DB_ESTOQUE = 9;
    // Offset (byte) da palavra de StatusOP, onde fica a flag RecebidoOP no bit 0.
    private static final int OFFSET_STATUS_OP = 0;
    // Offset (byte) da flag IniciarPedido (bit 0).
    private static final int OFFSET_INICIAR_PEDIDO = 62;
    // Offset (byte) da palavra de gerenciamento do estoque: RecebidoEstoque (bit 0) e IniciarGuardar (bit 1).
    private static final int OFFSET_GERENCIAMENTO_ESTOQUE = 64;
    // Offset (int) onde se grava a posição do magazine em que o CLP deve guardar o bloco.
    private static final int OFFSET_POSICAO_GUARDAR = 66;
    // Offset (byte) inicial do mapa de cores do magazine (28 posições, 1 byte cada).
    private static final int OFFSET_MAGAZINE = 68;

    private static final int BIT_RECEBIDO_OP = 0;
    private static final int BIT_INICIAR_PEDIDO = 0;
    private static final int BIT_RECEBIDO_ESTOQUE = 0;
    private static final int BIT_INICIAR_GUARDAR = 1;

    private PlcConnectionService plcConnectionService;
    private EstoqueInfoClp estoqueInfoClp;
    private AppStateConfig appStateConfig;
    private EstoqueService estoqueService;
    private PedidoService pedidoService;

    @Override
    public void onData(String ip, byte[] data) {
        processarLeitura(ip, data);
    }

    // Atualiza o estado da estação a partir do bloco bruto e aplica as regras de negócio.
    public void processarLeitura(String ip, byte[] dadosEstoque) {
        PlcConnector plcConnectorEst = plcConnectionService.getConnection(ip);
        if (plcConnectorEst == null) {
            return;
        }

        lerVariaveis(dadosEstoque);

        // Encadeamento das regras de negócio (cada uma lê estoqueInfoClp e,
        // se não estiver em readOnly, escreve as flags de volta no CLP).
        confirmarInicioPedido(plcConnectorEst);
        resetarRecebidoOp(plcConnectorEst);
        tratarInicioOperacao(plcConnectorEst);
        tratarFimOperacao(plcConnectorEst);
        resetarRecebidoEstoque(plcConnectorEst);
        removerBlocoDoEstoque(plcConnectorEst);
        adicionarBlocoAoEstoque(plcConnectorEst);
        resetarIniciarGuardar(plcConnectorEst);
        responderPedidoDePosicao(plcConnectorEst);
    }

    // Mapeia o bloco bruto do CLP ESTOQUE para o {@link EstoqueInfoClp}.
    private void lerVariaveis(byte[] dadosEstoque) {
        estoqueInfoClp.setRecebidoOp((dadosEstoque[0] & 0x01) != 0);

        estoqueInfoClp.setIniciarPedido((dadosEstoque[62] & (byte) 0x01) != 0);
        estoqueInfoClp.setRecebidoEstoque((dadosEstoque[64] & 0x01) != 0);
        estoqueInfoClp.setIniciarGuardarEst((dadosEstoque[64] & 0x02) != 0);

        estoqueInfoClp.setPosicaoGuardarEst(((dadosEstoque[66] & 0xFF) << 8) | (dadosEstoque[67] & 0xFF));

        byte[] posicoesOcupadas = new byte[28];
        for (int c = 0; c < 28; c++) {
            posicoesOcupadas[c] = dadosEstoque[OFFSET_MAGAZINE + c];
        }
        estoqueInfoClp.setPosicoesOcupadas(posicoesOcupadas);

        estoqueInfoClp.setNumeroOP(((dadosEstoque[96] & 0xFF) << 8) | (dadosEstoque[97] & 0xFF));
        estoqueInfoClp.setCancelOP((dadosEstoque[98] & 0x01) != 0);
        estoqueInfoClp.setFinishOP((dadosEstoque[98] & 0x02) != 0);
        estoqueInfoClp.setStartOP((dadosEstoque[98] & 0x04) != 0);

        estoqueInfoClp.setOcupado((dadosEstoque[100] & 0x01) != 0);
        estoqueInfoClp.setAguardando((dadosEstoque[100] & 0x02) != 0);
        estoqueInfoClp.setManual((dadosEstoque[100] & 0x04) != 0);
        estoqueInfoClp.setEmergencia((dadosEstoque[100] & 0x08) != 0);

        estoqueInfoClp.setPedirPosicaoEst((dadosEstoque[102] & 0x01) != 0);
        estoqueInfoClp.setPosicaoEstoque(((dadosEstoque[104] & 0xFF) << 8) | (dadosEstoque[105] & 0xFF));
        estoqueInfoClp.setAdicionarEstoque((dadosEstoque[106] & 0x01) != 0);
        estoqueInfoClp.setRemoverEstoque((dadosEstoque[106] & 0x02) != 0);
        estoqueInfoClp.setRetornoEstoqueCheio((dadosEstoque[106] & 0x04) != 0);
        estoqueInfoClp.setCorGuardarEstoque(((dadosEstoque[108] & 0xFF) << 8) | (dadosEstoque[109] & 0xFF));
    }

    // iniciarPedido == true & ocupado == true:
    // o ESTOQUE confirmou o início do pedido e ficou OCUPADO. Marca pedidoEmCurso,
    // zera os status de estoque/produção e baixa a flag IniciarPedido para FALSE.
    private void confirmarInicioPedido(PlcConnector plcConnectorEst) {
        // Antes monitorava a flag ocupado, porém essa flag nunca fica true ao mesmo tempo que o 
        // iniciarPedido, e com um pooling >200ms ele só lê um tick dessa flag, então não tem perigo
        // de cair no if duas vezes, mas mesmo assim verifica a flag do pedido em curso para garantir 
        // que não está sendo feito 2 vezes
        if (estoqueInfoClp.isIniciarPedido() && !appStateConfig.isPedidoEmCurso()) {
            appStateConfig.setPedidoEmCurso(true);
            log.info("ESTOQUE: pedido em curso definido como TRUE (OP {})", estoqueInfoClp.getNumeroOP());
            appStateConfig.setStatusEstoque((byte) 0);
            appStateConfig.setStatusProducao((byte) 0);
            // UTC explícito: evita ambiguidade de fuso quando o frontend interpreta a string.
            appStateConfig.setRegistroInicioPedido(LocalDateTime.now(ZoneOffset.UTC));
            pedidoService.handleEntradaEstoque(estoqueInfoClp.getNumeroOP());
            if (!appStateConfig.isReadOnly()) {
                try {
                    log.debug("ESTOQUE: escrevendo DB{}:{}.{} = false (IniciarPedido)", DB_ESTOQUE, OFFSET_INICIAR_PEDIDO, BIT_INICIAR_PEDIDO);
                    plcConnectorEst.writeBit(DB_ESTOQUE, OFFSET_INICIAR_PEDIDO, BIT_INICIAR_PEDIDO, false);
                } catch (Exception e) {
                    log.error("ESTOQUE: erro ao baixar flag IniciarPedido [DB9:62.0]: {}", e.getMessage());
                }
            }
        }
    }

    // Nenhuma operação em andamento (start, finish e cancel todas em FALSE):
    // baixa a flag RecebidoOP para FALSE, deixando a estação pronta para a próxima OP.
    private void resetarRecebidoOp(PlcConnector plcConnectorEst) {
        if (!estoqueInfoClp.isStartOP() & !estoqueInfoClp.isFinishOP() & !estoqueInfoClp.isCancelOP()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    log.debug("ESTOQUE: escrevendo DB{}:{}.{} = false (RecebidoOP)", DB_ESTOQUE, OFFSET_STATUS_OP, BIT_RECEBIDO_OP);
                    plcConnectorEst.writeBit(DB_ESTOQUE, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, false);
                } catch (Exception e) {
                    log.error("ESTOQUE: erro ao baixar flag RecebidoOP [DB9:0.0]: {}", e.getMessage());
                }
            }
        }
    }

    // Início da operação (startOP == true & recebidoOp == false):
    // marca statusEstoque = 1 (se há pedido em curso) e confirma a recepção da OP
    // subindo a flag RecebidoOP para TRUE.
    private void tratarInicioOperacao(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.isStartOP() & !estoqueInfoClp.isRecebidoOp()) {
            if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso()) {
                appStateConfig.setStatusEstoque((byte) 1);
            }
            if (!appStateConfig.isReadOnly()) {
                try {
                    log.debug("ESTOQUE: escrevendo DB{}:{}.{} = true (RecebidoOP startOP)", DB_ESTOQUE, OFFSET_STATUS_OP, BIT_RECEBIDO_OP);
                    plcConnectorEst.writeBit(DB_ESTOQUE, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                } catch (Exception e) {
                    log.error("ESTOQUE [startOp]: erro ao subir flag RecebidoOP [DB9:0.0]: {}", e.getMessage());
                }
            }
        }
    }

    // Fim da operação (finishOP == true & recebidoOp == false):
    // confirma a recepção subindo RecebidoOP para TRUE e marca statusEstoque = 2
    // (se há pedido em curso).
    private void tratarFimOperacao(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.isFinishOP() & !estoqueInfoClp.isRecebidoOp()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    log.debug("ESTOQUE: escrevendo DB{}:{}.{} = true (RecebidoOP finishOP)", DB_ESTOQUE, OFFSET_STATUS_OP, BIT_RECEBIDO_OP);
                    plcConnectorEst.writeBit(DB_ESTOQUE, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                } catch (Exception e) {
                    log.error("ESTOQUE [finishOp]: erro ao subir flag RecebidoOP [DB9:0.0]: {}", e.getMessage());
                }
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso()) {
                    appStateConfig.setStatusEstoque((byte) 2);
                }
            }
        }
    }

    // Nenhuma movimentação pendente (remover e adicionar ambas em FALSE):
    // baixa a flag RecebidoEstoque para FALSE.
    private void resetarRecebidoEstoque(PlcConnector plcConnectorEst) {
        if (!estoqueInfoClp.isRemoverEstoque() & !estoqueInfoClp.isAdicionarEstoque()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    log.debug("ESTOQUE: escrevendo DB{}:{}.{} = false (RecebidoEstoque)", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_RECEBIDO_ESTOQUE);
                    plcConnectorEst.writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_RECEBIDO_ESTOQUE, false);
                } catch (Exception e) {
                    log.error("ESTOQUE: erro ao baixar flag RecebidoEstoque [DB9:64.0]: {}", e.getMessage());
                }
            }
        }
    }

    // posicaoEstoque > 0 & removerEstoque == true:
    // confirma a movimentação (RecebidoEstoque = TRUE), zera a cor da posição no
    // mapa do magazine (offset = 68 + posicao - 1) e persiste a remoção na API.
    private void removerBlocoDoEstoque(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.getPosicaoEstoque() > 0 && estoqueInfoClp.isRemoverEstoque()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    log.debug("ESTOQUE: escrevendo DB{}:{}.{} = true (RecebidoEstoque remover)", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_RECEBIDO_ESTOQUE);
                    plcConnectorEst.writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_RECEBIDO_ESTOQUE, true);
                } catch (Exception e) {
                    log.error("ESTOQUE: erro ao subir flag RecebidoEstoque [DB9:64.0] (remover): {}", e.getMessage());
                }

                byte offset = (byte) (OFFSET_MAGAZINE + (estoqueInfoClp.getPosicaoEstoque() - 1));

                try {
                    log.info("ESTOQUE: removendo bloco da posição {} (DB{}:{})", estoqueInfoClp.getPosicaoEstoque(), DB_ESTOQUE, offset);
                    plcConnectorEst.writeByte(DB_ESTOQUE, offset, (byte) 0);
                    estoqueService.assignBlockColorByPosicaoFisica(estoqueInfoClp.getPosicaoEstoque(), CorEstoque.VAZIO);
                } catch (Exception e) {
                    log.error("ESTOQUE: erro ao remover bloco da posição {}: {}", estoqueInfoClp.getPosicaoEstoque(), e.getMessage(), e);
                }
            }
        }
    }

    // posicaoEstoque > 0 & adicionarEstoque == true:
    // confirma a movimentação (RecebidoEstoque = TRUE), grava a cor do bloco na
    // posição do magazine (offset = 68 + posicao - 1) e persiste a adição na API.
    private void adicionarBlocoAoEstoque(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.getPosicaoEstoque() > 0 && estoqueInfoClp.isAdicionarEstoque()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    log.debug("ESTOQUE: escrevendo DB{}:{}.{} = true (RecebidoEstoque adicionar)", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_RECEBIDO_ESTOQUE);
                    plcConnectorEst.writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_RECEBIDO_ESTOQUE, true);
                } catch (Exception e) {
                    log.error("ESTOQUE: erro ao subir flag RecebidoEstoque [DB9:64.0] (adicionar): {}", e.getMessage());
                }

                byte offset = (byte) (OFFSET_MAGAZINE + (estoqueInfoClp.getPosicaoEstoque() - 1));

                try {
                    log.info("ESTOQUE: adicionando bloco cor={} na posição {} (DB{}:{})", estoqueInfoClp.getCorGuardarEstoque(), estoqueInfoClp.getPosicaoEstoque(), DB_ESTOQUE, offset);
                    plcConnectorEst.writeByte(DB_ESTOQUE, offset, (byte) estoqueInfoClp.getCorGuardarEstoque());
                    estoqueService.assignBlockColorByPosicaoFisica(
                        estoqueInfoClp.getPosicaoEstoque(),
                        CorEstoque.fromValue(estoqueInfoClp.getCorGuardarEstoque())
                    );
                } catch (Exception e) {
                    log.error("ESTOQUE: erro ao adicionar bloco na posição {}: {}", estoqueInfoClp.getPosicaoEstoque(), e.getMessage(), e);
                }
            }
        }
    }

    // (ocupado == true | retornoEstoqueCheio == true) & iniciarGuardarEst == true:
    // a rotina de guardar já foi atendida (ou não há espaço); baixa a flag
    // IniciarGuardar para FALSE.
    private void resetarIniciarGuardar(PlcConnector plcConnectorEst) {
        if ((estoqueInfoClp.isOcupado() | estoqueInfoClp.isRetornoEstoqueCheio()) & estoqueInfoClp.isIniciarGuardarEst()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    log.debug("ESTOQUE: escrevendo DB{}:{}.{} = false (IniciarGuardar)", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_INICIAR_GUARDAR);
                    plcConnectorEst.writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_INICIAR_GUARDAR, false);
                } catch (Exception e) {
                    log.error("ESTOQUE: erro ao baixar flag IniciarGuardar [DB9:64.1]: {}", e.getMessage());
                }
            }
        }
    }

    // pedirPosicaoEst == true & ocupado == false:
    // o ESTOQUE está livre e pediu uma posição para guardar. Localiza a primeira
    // posição livre no magazine, grava-a em PosicaoGuardar [DB9:66] e sobe a flag
    // IniciarGuardar para TRUE.
    private void responderPedidoDePosicao(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.isPedirPosicaoEst() & !estoqueInfoClp.isOcupado()) {
            if (!appStateConfig.isReadOnly()) {
                Estoque primeiraPosicaoLivre = estoqueService.findByCorEstoque(CorEstoque.VAZIO).get(0);
                if (primeiraPosicaoLivre != null) {
                    try {
                        log.info("ESTOQUE: respondendo pedido de posição -> posição {} (DB{}:{})", primeiraPosicaoLivre.getPosicaoFisica(), DB_ESTOQUE, OFFSET_POSICAO_GUARDAR);
                        plcConnectorEst.writeInt(DB_ESTOQUE, OFFSET_POSICAO_GUARDAR, primeiraPosicaoLivre.getPosicaoFisica());
                    } catch (Exception e) {
                        log.error("ESTOQUE: erro ao escrever PosicaoGuardar [DB9:66]: {}", e.getMessage());
                    }

                    try {
                        log.debug("ESTOQUE: escrevendo DB{}:{}.{} = true (IniciarGuardar)", DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_INICIAR_GUARDAR);
                        plcConnectorEst.writeBit(DB_ESTOQUE, OFFSET_GERENCIAMENTO_ESTOQUE, BIT_INICIAR_GUARDAR, true);
                    } catch (Exception e) {
                        log.error("ESTOQUE: erro ao subir flag IniciarGuardar [DB9:64.1]: {}", e.getMessage());
                    }
                } else {
                    log.error("ESTOQUE: nenhuma posição livre disponível no magazine");
                }
            }
        }
    }
}
