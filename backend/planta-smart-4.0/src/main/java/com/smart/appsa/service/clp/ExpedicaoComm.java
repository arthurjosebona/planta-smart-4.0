package com.smart.appsa.service.clp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.model.clp.ExpedicaoInfoClp;
import com.smart.appsa.service.ExpedicaoService;
import com.smart.appsa.service.clp.reader.PlcDataObserver;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ExpedicaoComm implements PlcDataObserver {
    private PlcConnectionService plcConnectionService;
    private ExpedicaoInfoClp expedicaoInfoClp;
    private AppStateConfig appStateConfig;
    private ExpedicaoService expedicaoService;

    @Override
    public void onData(String ip, byte[] data) {
        processData(ip, data);
    }

    public void processData(String ip, byte[] dadosClp4) {
        logLeitura(dadosClp4);

        PlcConnector plcConnectorExp = plcConnectionService.getConnection(ip);
        if (plcConnectorExp == null) {
            return;
        }

        lerVariaveis(dadosClp4);

        // Regras de negócio da estação EXPEDIÇÃO
        tratarResetRecebidoOp(plcConnectorExp);
        tratarStartOp(plcConnectorExp);
        tratarFinishOp(plcConnectorExp);
        tratarResetIniciarGuardar(plcConnectorExp);
        tratarPedirPosicaoGuardar(plcConnectorExp);
        tratarResetRecebidoExpedicao(plcConnectorExp);
        tratarAdicionarExpedicao(plcConnectorExp);
        tratarRemoverExpedicao(plcConnectorExp);
        tratarOperacaoFinalizada();
    }

    /** Apresentação no console da leitura bruta (em hexadecimal). */
    private void logLeitura(byte[] dadosClp4) {
        StringBuilder leituraClp4 = new StringBuilder();
        for (byte b : dadosClp4) {
            leituraClp4.append(String.format("%02X ", b));
        }
    }
    
    private void lerVariaveis(byte[] dadosClp4) {
        expedicaoInfoClp.setRecebidoOp((dadosClp4[0] & 0x01) != 0);

        expedicaoInfoClp.setRecebidoExpedicao((dadosClp4[2] & 0x01) != 0);
        expedicaoInfoClp.setIniciarGuardarExp((dadosClp4[2] & 0x02) != 0);
        expedicaoInfoClp.setPosicaoGuardarExp(((dadosClp4[4] & 0xFF) << 8) | (dadosClp4[5] & 0xFF));

        // 12 posições do magazine, 2 bytes cada, a partir do offset 6
        int[] orderExpedicao = new int[12];
        int x = 0;
        for (int c = 0; c < 24; c += 2) {
            orderExpedicao[x] = (int) ((dadosClp4[c + 6] & 0xFF) << 8) | (dadosClp4[c + 7] & 0xFF);
            x++;
        }
        expedicaoInfoClp.setOrderExpedicao(orderExpedicao);

        expedicaoInfoClp.setNumeroOP(((dadosClp4[30] & 0xFF) << 8) | (dadosClp4[31] & 0xFF));
        expedicaoInfoClp.setCancelOP((dadosClp4[32] & 0x01) != 0);
        expedicaoInfoClp.setFinishOP((dadosClp4[32] & 0x02) != 0);
        expedicaoInfoClp.setStartOP((dadosClp4[32] & 0x04) != 0);

        expedicaoInfoClp.setOcupado((dadosClp4[34] & 0x01) != 0);
        expedicaoInfoClp.setAguardando((dadosClp4[34] & 0x02) != 0);
        expedicaoInfoClp.setManual((dadosClp4[34] & 0x04) != 0);
        expedicaoInfoClp.setEmergencia((dadosClp4[34] & 0x08) != 0);

        expedicaoInfoClp.setPedirPosicaoExp((dadosClp4[36] & 0x01) != 0);
        expedicaoInfoClp.setPosicaoGuardadoExpedicao(((dadosClp4[38] & 0xFF) << 8) | (dadosClp4[39] & 0xFF));
        expedicaoInfoClp.setPosicaoRemovidoExpedicao(((dadosClp4[40] & 0xFF) << 8) | (dadosClp4[41] & 0xFF));
        expedicaoInfoClp.setAdicionarExpedicao((dadosClp4[42] & 0x01) != 0);
        expedicaoInfoClp.setRemoverExpedicao((dadosClp4[42] & 0x02) != 0);
        expedicaoInfoClp.setOpGuardadoExpedicao(((dadosClp4[44] & 0xFF) << 8) | (dadosClp4[45] & 0xFF));
    }

    /**
     * StartOPExp, FinishOPExp e CancelOPExp todas em FALSE:
     * baixa a flag RecebidoOPExp [DB9:0.0] para FALSE.
     */
    private void tratarResetRecebidoOp(PlcConnector plcConnectorExp) {
        if (expedicaoInfoClp.isStartOP() == false & expedicaoInfoClp.isFinishOP() == false & expedicaoInfoClp.isCancelOP() == false) {
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorExp.writeBit(9, 0, 0, Boolean.parseBoolean("FALSE")); // coloca RecebidoOPExp em FALSE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [startOp][finishOp]: Atualização da Flag RecebidoOPExp [DB9:0.0] para FALSE");
                }
            }
        }
    }

    /**
     * startOP == true & recebidoOp == false:
     * pedido finalizado pela MONTAGEM e EXPEDIÇÃO iniciou a operação ->
     * statusExpedicao = 1 (se há pedido em curso) e sobe a flag RecebidoOPExp [DB9:0.0]
     * para TRUE.
     */
    private void tratarStartOp(PlcConnector plcConnectorExp) {
        if (expedicaoInfoClp.isStartOP() == true & expedicaoInfoClp.isRecebidoOp() == false) {
            if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso() == true) {
                appStateConfig.setStatusExpedicao((byte) 1);
            } else {
                //statusExpedicao = 0;
            }
            // blockFinished = true;
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorExp.writeBit(9, 0, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoOPExp em TRUE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [startOp]: Atualização da Flag RecebidoOPExp [DB9:0.0] para TRUE");
                }
            }
        }
    }

    /**
     * finishOP == true & recebidoOp == false:
     * EXPEDIÇÃO sinalizou o término da operação e ficou OCUPADO -> sobe RecebidoOPExp
     * [DB9:0.0] para TRUE, marca blockFinished e statusExpedicao = 2 (se há pedido em curso).
     */
    private void tratarFinishOp(PlcConnector plcConnectorExp) {
        if (expedicaoInfoClp.isFinishOP() == true & expedicaoInfoClp.isRecebidoOp() == false) {
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorExp.writeBit(9, 0, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoOPExp em TRUE
                    appStateConfig.setBlockFinished(true);
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [finishOp]: Atualização da Flag RecebidoOPExp [DB9:0.0] para TRUE");
                }
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso() == true) {
                    appStateConfig.setStatusExpedicao((byte) 2);
                } else {
                    //statusExpedicao = 0;
                }
            }
        }
    }

    /**
     * pedirPosicaoExp == false:
     * limpa o auxiliar de expedição e baixa a flag IniciarGuardar [DB9:2.1] para FALSE.
     */
    private void tratarResetIniciarGuardar(PlcConnector plcConnectorExp) {
        if (expedicaoInfoClp.isPedirPosicaoExp() == false) {
            if (!appStateConfig.isReadOnly()) {
                appStateConfig.setAux_expedicao(false);
                // Coloca a flag IniciarGuardar em FALSE
                try {
                    plcConnectorExp.writeBit(9, 2, 1, Boolean.parseBoolean("FALSE")); // coloca IniciarGuardar em FALSE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [Pedir Posição]: Atualização da Flag IniciarGuardar [DB9:2.1] para FALSE");
                }
            }
        }
    }

    /**
     * pedirPosicaoExp == true & aux_expedicao == false:
     * EXPEDIÇÃO pede posição para guardar o bloco concluído. Grava a posição solicitada
     * em PosicaoGuardarExpedicao [DB9:4] e sobe a flag IniciarGuardar [DB9:2.1] para TRUE.
     */
    private void tratarPedirPosicaoGuardar(PlcConnector plcConnectorExp) {
        if ((expedicaoInfoClp.isPedirPosicaoExp() == true) & appStateConfig.isAux_expedicao() == false) {
            // Rotina para localizar posição disponível no magazine da EXPEDIÇÃO
            appStateConfig.setAux_expedicao(true);

            if (!appStateConfig.isReadOnly()) {
                // Posição disponível para guardar (0-LIVRE 1-OCUPADA)
                try {
                    plcConnectorExp.writeInt(9, 4, appStateConfig.getPosicaoExpedicaoSolicitada()); // Atualiza PosicaoGuardarExpedicao no CLP
                } catch (Exception e) {
                    System.out.println("ERRO: Atualização da PosicaoGuardarExpedicao [DB9:4]");
                }

                try {
                    plcConnectorExp.writeBit(9, 2, 1, Boolean.parseBoolean("TRUE")); // coloca IniciarGuardar em TRUE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [Pedir Posição]: Atualização da Flag IniciarGuardar [DB9:2.1] para TRUE");
                }
            }
        }
    }

    /**
     * !adicionarExpedicao || !removerExpedicao:
     * baixa a flag RecebidoExpedicao [DB9:2.0] para FALSE.
     */
    private void tratarResetRecebidoExpedicao(PlcConnector plcConnectorExp) {
        if (!appStateConfig.isReadOnly() & (!expedicaoInfoClp.isAdicionarExpedicao() || !expedicaoInfoClp.isRemoverExpedicao())) {
            try {
                plcConnectorExp.writeBit(9, 2, 0, false); // coloca RecebidoExpedicao em FALSE
            } catch (Exception e) {
                if (!expedicaoInfoClp.isAdicionarExpedicao() & !expedicaoInfoClp.isRemoverExpedicao()) {
                    System.out.println("ERRO [Adicionar e Remover Expedição]: Atualização da Flag RecebidoExpedicao [DB9:2.0] para FALSE");
                } else if (!expedicaoInfoClp.isAdicionarExpedicao()) {
                    System.out.println("ERRO [Adicionar Expedição]: Atualização da Flag RecebidoExpedicao [DB9:2.0] para FALSE");
                } else {
                    System.out.println("ERRO [Remover Expedição]: Atualização da Flag RecebidoExpedicao [DB9:2.0] para FALSE");
                }
            }
        }
    }

    /**
     * adicionarExpedicao == true & aux_expedicao == false:
     * sobe RecebidoExpedicao [DB9:2.0], grava a OP guardada na posição do magazine
     * (offset = 6 + (posicaoGuardarExp - 1) * 2) e persiste a adição na API.
     */
    private void tratarAdicionarExpedicao(PlcConnector plcConnectorExp) {
        if ((expedicaoInfoClp.isAdicionarExpedicao() == true) & appStateConfig.isAux_expedicao() == false) {
            appStateConfig.setAux_expedicao(true);

            // Ler as variáveis PosicaoGuardadoExpedicao e opGuardadoExpedicao
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorExp.writeBit(9, 2, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoExpedicao em TRUE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [Adicionar Expedição]: Atualização da Flag RecebidoExpedicao [DB9:2.0] para TRUE");
                }

                int offset = 6 + (expedicaoInfoClp.getPosicaoGuardarExp() - 1) * 2;
                System.out.println("Guardando Operacao em posicaoGuardarExp: " + expedicaoInfoClp.getPosicaoGuardarExp());
                if (expedicaoInfoClp.getPosicaoGuardarExp() > 0) {
                    try {
                        // Atualiza cor no CLP
                        plcConnectorExp.writeInt(9, offset, expedicaoInfoClp.getOpGuardadoExpedicao());

                        expedicaoService.assignOrdemAtPosicao(expedicaoInfoClp.getOpGuardadoExpedicao(), expedicaoInfoClp.getPosicaoGuardarExp());
                    } catch (Exception e) {
                        System.out.println("ERRO: Na tentativa de adicionar na Expedição");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * removerExpedicao == true & aux_expedicao == false:
     * sobe RecebidoExpedicao [DB9:2.0], zera a OP na posição removida do magazine
     * (offset = 6 + (posicaoRemovidoExpedicao - 1) * 2) e persiste a remoção na API.
     */
    private void tratarRemoverExpedicao(PlcConnector plcConnectorExp) {
        if ((expedicaoInfoClp.isRemoverExpedicao() == true) & appStateConfig.isAux_expedicao() == false) {
            appStateConfig.setAux_expedicao(true);

            // Ler a variável PosicaoRemovidoExpedicao
            // posicaoRemovidoExpedicao = ((dadosClp4[40] & 0xFF) << 8) | (dadosClp4[41] & 0xFF);
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorExp.writeBit(9, 2, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoOPExpedicao em TRUE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [Adicionar Expedição]: Atualização da Flag RecebidoExpedicao [DB9:2.0] para TRUE");
                }

                int offset = 6 + (expedicaoInfoClp.getPosicaoRemovidoExpedicao() - 1) * 2;
                System.out.println("Removendo Operacao de posicaoREmovidoExpedicao: " + expedicaoInfoClp.getPosicaoRemovidoExpedicao());

                if (expedicaoInfoClp.getPosicaoRemovidoExpedicao() > 0 && !appStateConfig.isReadOnly()) {
                    try {
                        // Atualiza cor no CLP
                        plcConnectorExp.writeInt(9, offset, 0);

                        // Persiste a posição liberada (OP 0) via API
                        Map<String, Integer> dadosMap = new HashMap<>();
                        dadosMap.put("OP:" + expedicaoInfoClp.getPosicaoRemovidoExpedicao(), 0);

                        expedicaoService.assignOrdemAtPosicao(expedicaoInfoClp.getPosicaoRemovidoExpedicao(), 0);;
                    } catch (Exception e) {
                        System.out.println("ERRO: Na tentativa de remover da Expedição");
                        e.printStackTrace();
                    }
                }
            }
            // adicionaRemoveMagazineExpedicao(posicaoRemovidoExpedicao, 0);
            // updatePlcExpedicao();
        }
    }

    /**
     * posicaoGuardadoExpedicao == posicaoGuardarExp & ocupado == false & finishOP == true:
     * a operação foi guardada na posição correta com a estação livre -> marca
     * statusProducao = 1 (se há pedido em curso), encerrando o ciclo do pedido.
     */
    private void tratarOperacaoFinalizada() {
        if ((expedicaoInfoClp.getPosicaoGuardadoExpedicao() == expedicaoInfoClp.getPosicaoGuardarExp()) & (expedicaoInfoClp.isOcupado() == false) & (expedicaoInfoClp.isFinishOP() == true)) {
            if (appStateConfig.isReadOnly() == false) {
                System.out.println("AQUI: statusProducao: " + appStateConfig.getStatusProducao());
                System.out.println("AQUI: pedidoEmCurso:: " + appStateConfig.isPedidoEmCurso());
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso() == true) {
                    System.out.println("--------------------------------------------------");
                    System.out.println(" ");
                    //pedidoEmCurso = false;
                    appStateConfig.setStatusProducao((byte) 1);
                }
                System.out.println("Operação OP:" + expedicaoInfoClp.getOpGuardadoExpedicao() + " Finalizada: ");
            }
        }
    }
}
