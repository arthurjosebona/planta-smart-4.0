package com.smart.appsa.service.clp;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.config.ClpIpConfig;
import com.smart.appsa.events.PedidoConcluidoEvent;
import com.smart.appsa.events.PedidoEmCursoEvent;
import com.smart.appsa.events.UpdateExpedicaoEvent;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.model.clp.ExpedicaoInfoClp;
import com.smart.appsa.service.ExpedicaoService;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.clp.reader.PlcDataObserver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Handler da estação EXPEDIÇÃO (CLP 4 / DB9).
//
// <p>Registrado como {@link PlcDataObserver} no {@code PlcReaderTask} da estação,
// recebe a cada ciclo o bloco bruto lido do CLP, atualiza o {@link ExpedicaoInfoClp}
// e aplica as regras de negócio da expedição: sincronização das flags de operação,
// atendimento ao pedido de posição livre, adição/remoção de pedidos no magazine e
// conclusão do pedido quando o bloco é guardado.
//
// <p>Toda escrita no CLP é condicionada a {@code !appStateConfig.isReadOnly()}:
// em modo somente-leitura a aplicação observa, mas nunca escreve de volta.
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedicaoComm implements PlcDataObserver {
    // Data Block da estação EXPEDIÇÃO.
    private static final int DB_EXPEDICAO = 9;
    // Offset (byte) da palavra de StatusOP, onde fica a flag RecebidoOP no bit 0.
    private static final int OFFSET_STATUS_OP = 0;
    // Offset (byte) da palavra de gerenciamento: RecebidoExpedicao (bit 0) e IniciarGuardar (bit 1).
    private static final int OFFSET_GERENCIAMENTO_EXPEDICAO = 2;
    // Offset (int) onde se grava a posição do magazine em que o CLP deve guardar o bloco.
    private static final int OFFSET_POSICAO_GUARDAR = 4;
    // Offset (int) inicial do magazine de expedição (12 posições, 2 bytes cada).
    private static final int OFFSET_MAGAZINE = 6;
    private final ClpIpConfig clpIpConfig;

    private static final int BIT_RECEBIDO_OP = 0;
    private static final int BIT_RECEBIDO_EXPEDICAO = 0;
    private static final int BIT_INICIAR_GUARDAR = 1;

    private final PlcConnectionService plcConnectionService;
    private final ExpedicaoInfoClp expedicaoInfoClp;
    private final AppStateConfig appStateConfig;
    private final ExpedicaoService expedicaoService;
    private final PedidoService pedidoService;
    
    private final ApplicationEventPublisher eventPublisher;

    private int opAntiga = 0;

    @Override
    public void onData(String ip, byte[] data) {
        processarLeitura(ip, data);
    }

    // Atualiza o estado da estação a partir do bloco bruto e aplica as regras de negócio.
    public void processarLeitura(String ip, byte[] dadosExpedicao) {
        PlcConnector plcConnectorExp = plcConnectionService.getConnection(ip);
        if (plcConnectorExp == null) {
            return;
        }

        lerVariaveis(dadosExpedicao);

        // Encadeamento das regras de negócio da estação EXPEDIÇÃO.
        resetarRecebidoOp(plcConnectorExp);
        tratarInicioOperacao(plcConnectorExp);
        tratarFimOperacao(plcConnectorExp);
        gerenciarPosicaoGuardar(plcConnectorExp);
        resetarRecebidoExpedicao(plcConnectorExp);
        adicionarOpNaExpedicao(plcConnectorExp);
        removerOpDaExpedicao(plcConnectorExp);
        verifyOpAntiga();
        marcarOperacaoFinalizada();
        // concluirPedido();
        handleExpedicaoGuardado();
    }

    // Mapeia o bloco bruto do CLP EXPEDIÇÃO para o {@link ExpedicaoInfoClp}.
    private void lerVariaveis(byte[] dadosExpedicao) {
        expedicaoInfoClp.setRecebidoOp((dadosExpedicao[0] & 0x01) != 0);

        expedicaoInfoClp.setRecebidoExpedicao((dadosExpedicao[2] & 0x01) != 0);
        expedicaoInfoClp.setIniciarGuardarExp((dadosExpedicao[2] & 0x02) != 0);
        expedicaoInfoClp.setPosicaoGuardarExp(((dadosExpedicao[4] & 0xFF) << 8) | (dadosExpedicao[5] & 0xFF));

        // 12 posições do magazine, 2 bytes cada, a partir do offset 6.
        int[] orderExpedicao = new int[12];
        for (int i = 0; i < 12; i++) {
            int byteIndex = OFFSET_MAGAZINE + (i * 2);
            orderExpedicao[i] = ((dadosExpedicao[byteIndex] & 0xFF) << 8) | (dadosExpedicao[byteIndex + 1] & 0xFF);
        }
        expedicaoInfoClp.setOrderExpedicao(orderExpedicao);

        expedicaoInfoClp.setNumeroOP(((dadosExpedicao[30] & 0xFF) << 8) | (dadosExpedicao[31] & 0xFF));
        expedicaoInfoClp.setCancelOP((dadosExpedicao[32] & 0x01) != 0);
        expedicaoInfoClp.setFinishOP((dadosExpedicao[32] & 0x02) != 0);
        expedicaoInfoClp.setStartOP((dadosExpedicao[32] & 0x04) != 0);

        expedicaoInfoClp.setOcupado((dadosExpedicao[34] & 0x01) != 0);
        expedicaoInfoClp.setAguardando((dadosExpedicao[34] & 0x02) != 0);
        expedicaoInfoClp.setManual((dadosExpedicao[34] & 0x04) != 0);
        expedicaoInfoClp.setEmergencia((dadosExpedicao[34] & 0x08) != 0);

        expedicaoInfoClp.setPedirPosicaoExp((dadosExpedicao[36] & 0x01) != 0);
        expedicaoInfoClp.setPosicaoGuardadoExpedicao(((dadosExpedicao[38] & 0xFF) << 8) | (dadosExpedicao[39] & 0xFF));
        expedicaoInfoClp.setPosicaoRemovidoExpedicao(((dadosExpedicao[40] & 0xFF) << 8) | (dadosExpedicao[41] & 0xFF));
        expedicaoInfoClp.setAdicionarExpedicao((dadosExpedicao[42] & 0x01) != 0);
        expedicaoInfoClp.setRemoverExpedicao((dadosExpedicao[42] & 0x02) != 0);
        expedicaoInfoClp.setOpGuardadoExpedicao(((dadosExpedicao[44] & 0xFF) << 8) | (dadosExpedicao[45] & 0xFF));
    }

    // Nenhuma operação em andamento (start, finish e cancel todas em FALSE):
    // baixa a flag RecebidoOP para FALSE, deixando a estação pronta para a próxima OP.
    private void resetarRecebidoOp(PlcConnector plcConnectorExp) {
        if (!expedicaoInfoClp.isStartOP() & !expedicaoInfoClp.isFinishOP() & !expedicaoInfoClp.isCancelOP()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, false);
                } catch (Exception e) {
                    log.error("ERRO [idle]: Falha ao baixar RecebidoOPExp [DB9:0.0]: {}", e.getMessage());
                }
            }
        }
    }

    // Início da operação (startOP == true & recebidoOp == false):
    // a MONTAGEM finalizou o bloco e a EXPEDIÇÃO iniciou a operação. Marca
    // statusExpedicao = 1 (se há pedido em curso) e confirma a recepção da OP
    // subindo a flag RecebidoOP para TRUE.
    private void tratarInicioOperacao(PlcConnector plcConnectorExp) {
        if (expedicaoInfoClp.isStartOP() & !expedicaoInfoClp.isRecebidoOp()) {
            if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso()) {
                appStateConfig.setStatusExpedicao((byte) 1);
            }
            log.info("[EXPEDICAO] StartOP detectado — OP {}. Confirmando recepção.", expedicaoInfoClp.getNumeroOP());
            pedidoService.handleEntradaExpedicao(expedicaoInfoClp.getNumeroOP());
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                } catch (Exception e) {
                    log.error("ERRO [startOp]: Falha ao subir RecebidoOPExp [DB9:0.0]: {}", e.getMessage());
                }
            }
        }
    }

    // Fim da operação (finishOP == true & recebidoOp == false):
    // confirma a recepção subindo RecebidoOP para TRUE, marca blockFinished e
    // statusExpedicao = 2 (se há pedido em curso).
    private void tratarFimOperacao(PlcConnector plcConnectorExp) {
        if (expedicaoInfoClp.isFinishOP() & !expedicaoInfoClp.isRecebidoOp()) {
            log.info("[EXPEDICAO] FinishOP detectado — OP {}.", expedicaoInfoClp.getNumeroOP());
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                    appStateConfig.setBlockFinished(true);
                } catch (Exception e) {
                    log.error("ERRO [finishOp]: Falha ao subir RecebidoOPExp [DB9:0.0]: {}", e.getMessage());
                }
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso()) {
                    appStateConfig.setStatusExpedicao((byte) 2);
                }
            }
        }
    }

    // Gerencia a rotina de "guardar bloco" na expedição.
    //
    // <ul>
    //   <li>Se o CLP NÃO está pedindo posição: limpa o auxiliar e baixa a flag
    //       IniciarGuardar para FALSE.</li>
    //   <li>Se o CLP está pedindo posição e ainda não foi atendido neste ciclo
    //       ({@code aux_expedicao == false}): localiza a primeira posição livre do
    //       magazine, grava-a em PosicaoGuardar [DB9:4] e sobe a flag IniciarGuardar
    //       para TRUE.</li>
    // </ul>
    //
    // <p>Substitui os dois tratamentos anteriores que escreviam valores divergentes
    // (posição física x ordem de produção) no mesmo endereço a cada ciclo.
    private void gerenciarPosicaoGuardar(PlcConnector plcConnectorExp) {
        if (appStateConfig.isReadOnly()) {
            return;
        }

        if (!expedicaoInfoClp.isPedirPosicaoExp()) {
            appStateConfig.setAux_expedicao(false);
            try {
                plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, BIT_INICIAR_GUARDAR, false);
            } catch (Exception e) {
                log.error("ERRO [gerenciarPosicaoGuardar]: Falha ao baixar IniciarGuardar [DB9:2.1]: {}", e.getMessage());
            }
            return;
        }

        // Pedido de posição já atendido neste ciclo de solicitação: nada a fazer.
        if (appStateConfig.isAux_expedicao()) {
            return;
        }
        appStateConfig.setAux_expedicao(true);

        int posicaoExpedicaoSolicitada = expedicaoService.findFirstPosicaoLivre().getPosicaoFisica();
        appStateConfig.setPosicaoExpedicaoSolicitada(posicaoExpedicaoSolicitada);
        log.info("[EXPEDICAO] PedirPosição: posição livre encontrada = {}.", posicaoExpedicaoSolicitada);

        try {
            plcConnectorExp.writeInt(DB_EXPEDICAO, OFFSET_POSICAO_GUARDAR, posicaoExpedicaoSolicitada);
        } catch (Exception e) {
            log.error("ERRO: Falha ao gravar PosicaoGuardarExpedicao [DB9:4]: {}", e.getMessage());
        }

        try {
            plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, BIT_INICIAR_GUARDAR, true);
        } catch (Exception e) {
            log.error("ERRO [gerenciarPosicaoGuardar]: Falha ao subir IniciarGuardar [DB9:2.1]: {}", e.getMessage());
        }
    }

    // Nenhuma movimentação pendente (adicionar e remover ambas em FALSE):
    // baixa a flag RecebidoExpedicao para FALSE.
    private void resetarRecebidoExpedicao(PlcConnector plcConnectorExp) {
        if (!appStateConfig.isReadOnly() & (!expedicaoInfoClp.isAdicionarExpedicao() && !expedicaoInfoClp.isRemoverExpedicao())) {
            try {
                plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, BIT_RECEBIDO_EXPEDICAO, false);
            } catch (Exception e) {
                log.error("ERRO [resetarRecebidoExpedicao]: Falha ao baixar RecebidoExpedicao [DB9:2.0]: {}", e.getMessage());
            }
        }
    }

    // adicionarExpedicao == true & aux_expedicao == false:
    // confirma a movimentação (RecebidoExpedicao = TRUE), grava a OP guardada na
    // posição do magazine (offset = 6 + (posicaoGuardarExp - 1) * 2) e persiste a
    // adição na API.
    private void adicionarOpNaExpedicao(PlcConnector plcConnectorExp) {
        
        if (expedicaoInfoClp.isAdicionarExpedicao() & !appStateConfig.isAux_expedicao()) {
            log.info("[EXPEDICAO] AdicionarExpedicao — OP {} na posição {}.", expedicaoInfoClp.getOpGuardadoExpedicao(), expedicaoInfoClp.getPosicaoGuardarExp());
            appStateConfig.setAux_expedicao(true);

            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, BIT_RECEBIDO_EXPEDICAO, true);
                } catch (Exception e) {
                    log.error("ERRO [adicionarExpedicao]: Falha ao subir RecebidoExpedicao [DB9:2.0]: {}", e.getMessage());
                }

                if (expedicaoInfoClp.getPosicaoGuardarExp() > 0) {
                    int offset = OFFSET_MAGAZINE + (expedicaoInfoClp.getPosicaoGuardarExp() - 1) * 2;
                    try {
                        plcConnectorExp.writeInt(DB_EXPEDICAO, offset, expedicaoInfoClp.getOpGuardadoExpedicao());
                        expedicaoService.assignOrdemAtPosicao(
                            expedicaoInfoClp.getOpGuardadoExpedicao(),
                            expedicaoInfoClp.getPosicaoGuardarExp()
                        );
                        log.info("[EXPEDICAO] OP {} adicionada na posição {} com sucesso.", expedicaoInfoClp.getOpGuardadoExpedicao(), expedicaoInfoClp.getPosicaoGuardarExp());
                    } catch (Exception e) {
                        log.error("ERRO: Falha ao adicionar OP {} na Expedição posição {}: {}", expedicaoInfoClp.getOpGuardadoExpedicao(), expedicaoInfoClp.getPosicaoGuardarExp(), e.getMessage(), e);
                    }
                }
            }
        }
    }

    // removerExpedicao == true & aux_expedicao == false:
    // confirma a movimentação (RecebidoExpedicao = TRUE), zera a OP na posição
    // removida do magazine (offset = 6 + (posicaoRemovidoExpedicao - 1) * 2),
    // registra a saída do pedido e libera a posição na API.
    private void removerOpDaExpedicao(PlcConnector plcConnectorExp) {
        if (expedicaoInfoClp.isRemoverExpedicao() & !appStateConfig.isAux_expedicao()) {
            appStateConfig.setAux_expedicao(true);

            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, BIT_RECEBIDO_EXPEDICAO, true);
                } catch (Exception e) {
                    log.error("ERRO [removerExpedicao]: Falha ao subir RecebidoExpedicao [DB9:2.0]: {}", e.getMessage());
                }

                if (expedicaoInfoClp.getPosicaoRemovidoExpedicao() > 0) {
                    log.info("[EXPEDICAO] RemoverExpedicao — posição {}.", expedicaoInfoClp.getPosicaoRemovidoExpedicao());
                    int offset = OFFSET_MAGAZINE + (expedicaoInfoClp.getPosicaoRemovidoExpedicao() - 1) * 2;
                    try {
                        // A flag removerExpedicao permanece TRUE por vários ciclos de leitura, mas
                        // aux_expedicao é compartilhada e pode ser resetada por gerenciarPosicaoGuardar.
                        // Se a ordem da posição já foi zerada em um ciclo anterior, não há nada a remover:
                        // evita findByOp(0) -> ResourceNotFoundException.
                        int ordemAtual = expedicaoService.findByPosicaoFisica(
                            expedicaoInfoClp.getPosicaoRemovidoExpedicao()
                        ).getOrdemDeProducaoAtual();

                        if (ordemAtual <= 0) {
                            log.debug("[EXPEDICAO] RemoverExpedicao: posição {} já zerada, ignorando.", expedicaoInfoClp.getPosicaoRemovidoExpedicao());
                            return;
                        }

                        plcConnectorExp.writeInt(DB_EXPEDICAO, offset, 0);
                        pedidoService.handleExitExpedicao(ordemAtual);
                        expedicaoService.assignOrdemAtPosicao(expedicaoInfoClp.getPosicaoRemovidoExpedicao(), 0);
                        log.info("[EXPEDICAO] OP {} removida da posição {} com sucesso.", ordemAtual, expedicaoInfoClp.getPosicaoRemovidoExpedicao());
                    } catch (Exception e) {
                        log.error("ERRO: Falha ao remover da Expedição posição {}: {}", expedicaoInfoClp.getPosicaoRemovidoExpedicao(), e.getMessage(), e);
                    }
                }
            }
        }
    }


    private void verifyOpAntiga() {
        if (!appStateConfig.isPedidoEmCurso() && expedicaoInfoClp.getOpGuardadoExpedicao() != opAntiga) {
            opAntiga = expedicaoInfoClp.getOpGuardadoExpedicao();
        }
    }


    // posicaoGuardadoExpedicao == posicaoGuardarExp & ocupado == false & finishOP == true:
    // a operação foi guardada na posição correta com a estação livre. Marca
    // statusProducao = 1 (se há pedido em curso), encerrando o ciclo do pedido.
    private void marcarOperacaoFinalizada() {
        if ((expedicaoInfoClp.getPosicaoGuardadoExpedicao() == expedicaoInfoClp.getPosicaoGuardarExp())
                & !expedicaoInfoClp.isOcupado()
                & expedicaoInfoClp.isFinishOP()) {
            if (!appStateConfig.isReadOnly()) {
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso()) {
                    appStateConfig.setStatusProducao((byte) 1);
                }
                log.info("[EXPEDICAO] OP {} guardada na posição {} — operação finalizada.", expedicaoInfoClp.getOpGuardadoExpedicao(), expedicaoInfoClp.getPosicaoGuardadoExpedicao());
            }
        }
    }

    // Operação que consegue ser executada de acordo com as flags que são
    // lidas, utilizada para finalizar o pedido que está rolando, e escrever 
    // na expedição somente quando o pedido foi guardado realmente
    private void handleExpedicaoGuardado() {
        if (expedicaoInfoClp.getOpGuardadoExpedicao() <= 0) {
            return;
        }
        if (!appStateConfig.isPedidoEmCurso()) {
            return;
        }
        if (expedicaoInfoClp.getOpGuardadoExpedicao() == opAntiga) {
            return;
        }
        int opAtual = expedicaoInfoClp.getNumeroOP();
        log.debug("[EXPEDICAO] handleExpedicaoGuardado — opGuardado={}, opAtual={}.", expedicaoInfoClp.getOpGuardadoExpedicao(), opAtual);
        if (expedicaoInfoClp.getOpGuardadoExpedicao() == opAtual) {
            log.info("===== OP {} CONCLUÍDA — pedidoEmCurso → false =====", opAtual);
            appStateConfig.setPedidoEmCurso(false);
            eventPublisher.publishEvent(new PedidoEmCursoEvent(this, false));
            eventPublisher.publishEvent(new UpdateExpedicaoEvent(this, expedicaoInfoClp.getPosicaoGuardadoExpedicao(), opAtual));
            opAntiga = expedicaoInfoClp.getOpGuardadoExpedicao();
            pedidoService.updateToConcluido(
                PedidoMapper.mapEntityByResponseDTO(pedidoService.findByOp(opAtual))
            );
            appStateConfig.resetarStatus();
            // Conclusão real do pedido em curso: dispara o avanço da fila de produção.
            eventPublisher.publishEvent(new PedidoConcluidoEvent(this, opAtual));
        }
    }

    // Conclui o pedido no domínio quando a OP foi finalizada (ou o CLP já não está
    // mais com a OP recebida). O guarda {@link #opConcluidaAnterior} garante que cada
    // OP seja concluída uma única vez, evitando chamadas repetidas à API a cada ciclo.
    // private void concluirPedido() {
    //     if (((expedicaoInfoClp.isFinishOP() || !expedicaoInfoClp.isRecebidoExpedicao())
    //             && (expedicaoInfoClp.getOpGuardadoExpedicao() > 0 && expedicaoInfoClp.getOpGuardadoExpedicao() != opAntiga))) {
    //         opAntiga = expedicaoInfoClp.getOpGuardadoExpedicao();
    //         pedidoService.updateToConcluido(
    //             PedidoMapper.mapEntityByResponseDTO(pedidoService.findByOp(expedicaoInfoClp.getOpGuardadoExpedicao()))
    //         );
    //     }
    // }

    @Async("plcWriteExpedicaoExecutor")
    @EventListener
    public void atualizarPosicaoExpedicao(UpdateExpedicaoEvent event) {
        log.info("UpdateExpedicaoEvent recebido — posição={}, codPedido={}.", event.getPosicao(), event.getCodPedido());
        PlcConnector connector = plcConnectionService.getConnection(clpIpConfig.getIp("expedicao"));
        if (connector == null) {
            log.warn("AVISO: CLP expedição desconectado — reserva posição {} pedido {} descartada.", event.getPosicao(), event.getCodPedido());
            return;
        }
        synchronized (connector) {
            try {
                connector.writeInt(DB_EXPEDICAO, 6 + (event.getPosicao() - 1) * 2, event.getCodPedido());
                log.debug("Reserva de expedição gravada: posição={}, codPedido={}.", event.getPosicao(), event.getCodPedido());
            } catch (Exception e) {
                log.error("ERRO: Falha ao gravar reserva de expedição posição {}: {}", event.getPosicao(), e.getMessage(), e);
            }
        }
    }

    public void printHex(byte[] bytes) {
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("--- BLOCO DE BYTES (HEX) ---\n");
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X ", bytes[i]));
                if ((i + 1) % 10 == 0) sb.append("\n");
            }
            sb.append("\n----------------------------");
            log.debug(sb.toString());
        }
    }
}
