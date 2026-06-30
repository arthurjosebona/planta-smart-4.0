package com.smart.appsa.service.clp;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.config.ClpIpConfig;
import com.smart.appsa.events.UpdateExpedicaoEvent;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.clp.ExpedicaoInfoClp;
import com.smart.appsa.service.ExpedicaoService;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.clp.reader.PlcDataObserver;

import lombok.RequiredArgsConstructor;

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
        handleEstoqueGuardado();
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
                    System.out.println(
                            "ERRO [startOp][finishOp]: Atualização da Flag RecebidoOPExp [DB9:0.0] para FALSE");
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
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [startOp]: Atualização da Flag RecebidoOPExp [DB9:0.0] para TRUE");
                }
            }
        }
    }

    // Fim da operação (finishOP == true & recebidoOp == false):
    // confirma a recepção subindo RecebidoOP para TRUE, marca blockFinished e
    // statusExpedicao = 2 (se há pedido em curso).
    private void tratarFimOperacao(PlcConnector plcConnectorExp) {
        if (expedicaoInfoClp.isFinishOP() & !expedicaoInfoClp.isRecebidoOp()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                    appStateConfig.setBlockFinished(true);
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [finishOp]: Atualização da Flag RecebidoOPExp [DB9:0.0] para TRUE");
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
                System.out.println(
                        "ERRO [Pedir Posição]: Atualização da Flag IniciarGuardar [DB9:2.1] para FALSE");
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

        try {
            plcConnectorExp.writeInt(DB_EXPEDICAO, OFFSET_POSICAO_GUARDAR, posicaoExpedicaoSolicitada);
        } catch (Exception e) {
            System.out.println("ERRO: Atualização da PosicaoGuardarExpedicao [DB9:4]");
        }

        try {
            plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, BIT_INICIAR_GUARDAR, true);
        } catch (Exception e) {
            System.out.println(
                    "ERRO [Pedir Posição]: Atualização da Flag IniciarGuardar [DB9:2.1] para TRUE");
        }
    }

    // Nenhuma movimentação pendente (adicionar e remover ambas em FALSE):
    // baixa a flag RecebidoExpedicao para FALSE.
    private void resetarRecebidoExpedicao(PlcConnector plcConnectorExp) {
        if (!appStateConfig.isReadOnly() & (!expedicaoInfoClp.isAdicionarExpedicao() && !expedicaoInfoClp.isRemoverExpedicao())) {
            try {
                plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, BIT_RECEBIDO_EXPEDICAO, false);
            } catch (Exception e) {
                System.out.println("ERRO [Adicionar/Remover Expedição]: Atualização da Flag RecebidoExpedicao [DB9:2.0] para FALSE");
            }
        }
    }

    // adicionarExpedicao == true & aux_expedicao == false:
    // confirma a movimentação (RecebidoExpedicao = TRUE), grava a OP guardada na
    // posição do magazine (offset = 6 + (posicaoGuardarExp - 1) * 2) e persiste a
    // adição na API.
    private void adicionarOpNaExpedicao(PlcConnector plcConnectorExp) {
        
        if (expedicaoInfoClp.isAdicionarExpedicao() & !appStateConfig.isAux_expedicao()) {
            System.out.printf("\n\n\n\n-------------------\nCHEGOU NO adicionarOpNaExpepdicao\n-----------------------\n\n\n\n\n");

            appStateConfig.setAux_expedicao(true);

            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorExp.writeBit(DB_EXPEDICAO, OFFSET_GERENCIAMENTO_EXPEDICAO, BIT_RECEBIDO_EXPEDICAO, true);
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [Adicionar Expedição]: Atualização da Flag RecebidoExpedicao [DB9:2.0] para TRUE");
                }

                if (expedicaoInfoClp.getPosicaoGuardarExp() > 0) {
                    int offset = OFFSET_MAGAZINE + (expedicaoInfoClp.getPosicaoGuardarExp() - 1) * 2;
                    try {
                        plcConnectorExp.writeInt(DB_EXPEDICAO, offset, expedicaoInfoClp.getOpGuardadoExpedicao());
                        expedicaoService.assignOrdemAtPosicao(
                            expedicaoInfoClp.getOpGuardadoExpedicao(),
                            expedicaoInfoClp.getPosicaoGuardarExp()
                        );
                    } catch (Exception e) {
                        System.out.println("ERRO: Na tentativa de adicionar na Expedição");
                        e.printStackTrace();
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
                    System.out.println(
                            "ERRO [Remover Expedição]: Atualização da Flag RecebidoExpedicao [DB9:2.0] para TRUE");
                }

                if (expedicaoInfoClp.getPosicaoRemovidoExpedicao() > 0) {
                    int offset = OFFSET_MAGAZINE + (expedicaoInfoClp.getPosicaoRemovidoExpedicao() - 1) * 2;
                    try {
                        plcConnectorExp.writeInt(DB_EXPEDICAO, offset, 0);

                        Pedido expedido = PedidoMapper.mapEntityByResponseDTO(
                            pedidoService.findByOp(
                                expedicaoService.findByPosicaoFisica(
                                    expedicaoInfoClp.getPosicaoRemovidoExpedicao()
                                ).getOrdemDeProducaoAtual()
                            )
                        );

                        pedidoService.handleExitExpedicao(expedido);
                        expedicaoService.assignOrdemAtPosicao(expedicaoInfoClp.getPosicaoRemovidoExpedicao(), 0);
                    } catch (Exception e) {
                        System.out.println("ERRO: Na tentativa de remover da Expedição");
                        e.printStackTrace();
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
                System.out.println("Operação OP:" + expedicaoInfoClp.getOpGuardadoExpedicao() + " Finalizada.");
            }
        }
    }

    // Operação que consegue ser executada de acordo com as flags que são
    // lidas, utilizada para finalizar o pedido que está rolando, e escrever 
    // na expedição somente quando o pedido foi guardado realmente
    private void handleEstoqueGuardado() {
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
        System.out.printf("\n\n\nPASSOU DAS VERIFICAÇÕES INICIAIS DE handleEstoqueGuardado, OP: " + opAtual + "\n\n\n\n");
        if (expedicaoInfoClp.getOpGuardadoExpedicao() == opAtual) {
            System.out.printf("\n\n\n\n-------------------\nCHEGOU NO handleEstoqueGuadrado\n-----------------------\n\n\n\n\n");
            System.out.println("DEFININDO PEDIDO EM CURSO PARA FALSE");
            appStateConfig.setPedidoEmCurso(false);
            eventPublisher.publishEvent(new UpdateExpedicaoEvent(this, expedicaoInfoClp.getPosicaoGuardadoExpedicao(), opAtual));
            opAntiga = expedicaoInfoClp.getOpGuardadoExpedicao();
            pedidoService.updateToConcluido(
                PedidoMapper.mapEntityByResponseDTO(pedidoService.findByOp(opAtual))
            );
            appStateConfig.resetarStatus();
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
        System.out.println("Evento de atualizar posição expedição");
        PlcConnector connector = plcConnectionService.getConnection(clpIpConfig.getIp("expedicao"));
        if (connector == null) {
            System.out.println("AVISO: CLP expedicao desconectado, reserva pos "  + event.getPosicao() + " pedido " + event.getCodPedido() + " descartada");
            return;
        }
        synchronized (connector) {
            try {
                connector.writeInt(DB_EXPEDICAO, 6 + (event.getPosicao() - 1) * 2, event.getCodPedido());
            } catch (Exception e) {
                System.out.println("ERRO: write reserva expedicao posicao " + event.getPosicao());
                e.printStackTrace();
            }
        }
    }

    public void printHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        System.out.println("--- BLOCO DE BYTES (HEXADECIMAL) ---");

        for (int i = 0; i < bytes.length; i++) {
            // Converte o byte para Hex e garante que tenha 2 dígitos (ex: 0A em vez de A)
            sb.append(String.format("%02X ", bytes[i]));

            // Opcional: Quebra de linha a cada 10 bytes para facilitar a leitura
            if ((i + 1) % 10 == 0) {
                sb.append("\n");
            }
        }

        System.out.println(sb.toString());
        System.out.println("------------------------------------");
    }
}
