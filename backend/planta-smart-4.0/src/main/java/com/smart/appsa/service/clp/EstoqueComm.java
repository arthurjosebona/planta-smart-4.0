package com.smart.appsa.service.clp;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.clp.EstoqueInfoClp;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.service.EstoqueService;
import com.smart.appsa.service.clp.reader.PlcDataObserver;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class EstoqueComm implements PlcDataObserver {
    private PlcConnectionService plcConnectionService;
    private EstoqueInfoClp estoqueInfoClp;
    private AppStateConfig appStateConfig;
    private EstoqueService estoqueService;

    @Override
    public void onData(String ip, byte[] data) {
        processData(ip, data);
    }

    public void processData(String ip, byte[] dadosClp1) {
        logLeitura(dadosClp1);

        PlcConnector plcConnectorEst = plcConnectionService.getConnection(ip);
        if (plcConnectorEst == null) {
            return;
        }

        lerVariaveis(dadosClp1);

        // Encadeamento das regras de negócio (cada uma lê estoqueInfoClp e,
        // se não estiver em readOnly, escreve as flags de volta no CLP).
        tratarInicioPedido(plcConnectorEst);
        tratarResetRecebidoOp(plcConnectorEst);
        tratarStartOp(plcConnectorEst);
        tratarFinishOp(plcConnectorEst);
        tratarResetRecebidoEstoque(plcConnectorEst);
        tratarRemoverEstoque(plcConnectorEst);
        tratarAdicionarEstoque(plcConnectorEst);
        tratarResetIniciarGuardar(plcConnectorEst);
        tratarPedirPosicaoGuardar(plcConnectorEst);
    }

    /** Apresentação no console da leitura bruta (em hexadecimal). */
    private void logLeitura(byte[] dadosClp1) {
        StringBuilder leituraClp1 = new StringBuilder();
        for (byte b : dadosClp1) {
            leituraClp1.append(String.format("%02X ", b));
        }
    }

    /** Lê todas as variáveis do bloco bruto do CLP ESTOQUE para {@link EstoqueInfoClp}. */
    private void lerVariaveis(byte[] dadosClp1) {
        estoqueInfoClp.setRecebidoOp((dadosClp1[0] & 0x01) != 0);

        estoqueInfoClp.setIniciarPedido((dadosClp1[62] & (byte) 0x01) != 0);
        estoqueInfoClp.setRecebidoEstoque((dadosClp1[64] & 0x01) != 0);
        estoqueInfoClp.setIniciarGuardarEst((dadosClp1[64] & 0x02) != 0);

        estoqueInfoClp.setPosicaoGuardarEst(((dadosClp1[66] & 0xFF) << 8) | (dadosClp1[67] & 0xFF));

        byte[] posicoesOcupadas = new byte[28];
        for (int c = 0; c < 28; c++) {
            posicoesOcupadas[c] = dadosClp1[68 + c];
        }
        estoqueInfoClp.setPosicoesOcupadas(posicoesOcupadas);

        estoqueInfoClp.setNumeroOP(((dadosClp1[96] & 0xFF) << 8) | (dadosClp1[97] & 0xFF));
        estoqueInfoClp.setCancelOP((dadosClp1[98] & 0x01) != 0);
        estoqueInfoClp.setFinishOP((dadosClp1[98] & 0x02) != 0);
        estoqueInfoClp.setStartOP((dadosClp1[98] & 0x04) != 0);

        estoqueInfoClp.setOcupado((dadosClp1[100] & 0x01) != 0);
        estoqueInfoClp.setAguardando((dadosClp1[100] & 0x02) != 0);
        estoqueInfoClp.setManual((dadosClp1[100] & 0x04) != 0);
        estoqueInfoClp.setEmergencia((dadosClp1[100] & 0x08) != 0);

        estoqueInfoClp.setPedirPosicaoEst((dadosClp1[102] & 0x01) != 0);
        estoqueInfoClp.setPosicaoEstoque(((dadosClp1[104] & 0xFF) << 8) | (dadosClp1[105] & 0xFF));
        estoqueInfoClp.setAdicionarEstoque((dadosClp1[106] & 0x01) != 0);
        estoqueInfoClp.setRemoverEstoque((dadosClp1[106] & 0x02) != 0);
        estoqueInfoClp.setRetornoEstoqueCheio((dadosClp1[106] & 0x04) != 0);
        estoqueInfoClp.setCorGuardarEstoque(((dadosClp1[108] & 0xFF) << 8) | (dadosClp1[109] & 0xFF));
        estoqueInfoClp.setRemoverEstoque((dadosClp1[106] & 0x02) != 0);
    }

    /**
     * iniciarPedido == true & ocupadoEst == true:
     * ESTOQUE confirmou o início do pedido e ficou OCUPADO -> marca pedidoEmCurso,
     * zera os status e baixa a flag IniciarPedido [DB9:62.0] para FALSE.
     */
    private void tratarInicioPedido(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.isIniciarPedido() == true && estoqueInfoClp.isOcupado() == true) {
            appStateConfig.setPedidoEmCurso(true);
            appStateConfig.setStatusEstoque((byte) 0);
            appStateConfig.setStatusProducao((byte) 0);
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorEst.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE")); // coloca IniciarPedido em FALSE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [iniciarPedido == true & ocupadoEst == true]: Atualização da Flag IniciarPedido [DB9:62.0] para FALSE");
                }
            }
        }
    }

    /**
     * StartOP, FinishOP e CancelOP todas em FALSE:
     * baixa a flag RecebidoOP [DB9:0.0] para FALSE.
     */
    private void tratarResetRecebidoOp(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.isStartOP() == false & estoqueInfoClp.isFinishOP() == false & estoqueInfoClp.isCancelOP() == false) {
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorEst.writeBit(9, 0, 0, Boolean.parseBoolean("FALSE")); // coloca RecebidoOPEst em FALSE
                } catch (Exception e) {
                    System.out.println("ERRO: Atualização da Flag RecebidoOPEstoque [DB9:0.0] para FALSE");
                }
            }
        }
    }

    /**
     * startOP == true & recebidoOp == false:
     * ESTOQUE sinalizou o início da operação -> statusEstoque = 1 (se há pedido em curso)
     * e sobe a flag RecebidoOP [DB9:0.0] para TRUE.
     */
    private void tratarStartOp(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.isStartOP() == true & estoqueInfoClp.isRecebidoOp() == false) {
            if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso() == true) {
                appStateConfig.setStatusEstoque((byte) 1);
            } else {
                //statusEstoque = 0;
            }
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorEst.writeBit(9, 0, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoOPEst em TRUE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [startOp]: Atualização da Flag RecebidoOPEstoque [DB9:0.0] para TRUE");
                }
            }
        }
    }

    /**
     * finishOP == true & recebidoOp == false:
     * ESTOQUE sinalizou o término da operação -> sobe a flag RecebidoOP [DB9:0.0] para
     * TRUE e marca statusEstoque = 2 (se há pedido em curso).
     */
    private void tratarFinishOp(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.isFinishOP() == true & estoqueInfoClp.isRecebidoOp() == false) {
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorEst.writeBit(9, 0, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoOPEst em TRUE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO [finishOp]: Atualização da Flag RecebidoOPEstoque [DB9:0.0] para TRUE");
                }
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso() == true) {
                    appStateConfig.setStatusEstoque((byte) 2);
                } else {
                    //statusEstoque = 0;
                }
            }
        }
    }

    /**
     * removerEstoque == false & adicionarEstoque == false:
     * baixa a flag RecebidoEstoque [DB9:64.0] para FALSE.
     */
    private void tratarResetRecebidoEstoque(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.isRemoverEstoque() == false & estoqueInfoClp.isAdicionarEstoque() == false) {
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorEst.writeBit(9, 64, 0, Boolean.parseBoolean("FALSE")); // coloca RecebidoEstoque em FALSE
                } catch (Exception e) {
                    System.out.println("ERRO: Atualização da Flag RecebidoEstoque [DB9:64.0] para FALSE");
                }
            }
        }
    }

    /**
     * posicaoEstoque > 0 & removerEstoque == true:
     * sobe RecebidoEstoque [DB9:64.0], zera a cor da posição no CLP
     * (offset = 68 + posicao - 1) e persiste a remoção na API.
     */
    private void tratarRemoverEstoque(PlcConnector plcConnectorEst) {
        if ((estoqueInfoClp.getPosicaoEstoque() > 0) && estoqueInfoClp.isRemoverEstoque() == true) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorEst.writeBit(9, 64, 0, true);
                } catch (Exception e) {
                    System.out.println("ERRO: Atualização da Flag RecebidoEstoque [DB9:64.0] para TRUE");
                }

                byte offset = (byte) (68 + (estoqueInfoClp.getPosicaoEstoque() - 1));

                try {
                    // Atualiza cor no CLP
                    System.out.println("\n\nREMOVENDO ESTOQUE NA POSIÇÃO: " + offset + " COR: " + estoqueInfoClp.getCorGuardarEstoque() + "\n\n");
                    plcConnectorEst.writeByte(9, offset, (byte) 0);

                    // Persiste a posição na API
                    estoqueService.assignBlockColorByPosicaoFisica(estoqueInfoClp.getPosicaoEstoque(), CorEstoque.VAZIO);
                } catch (Exception e) {
                    System.out.println("ERRO: Na tentativa de remover do Estoque");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * posicaoEstoque > 0 & adicionarEstoque == true:
     * sobe RecebidoEstoque [DB9:64.0], grava a cor do bloco na posição do CLP
     * (offset = 68 + posicao - 1) e persiste a adição na API.
     */
    private void tratarAdicionarEstoque(PlcConnector plcConnectorEst) {
        if ((estoqueInfoClp.getPosicaoEstoque() > 0) && estoqueInfoClp.isAdicionarEstoque() == true) {
            if (appStateConfig.isReadOnly() == false) {
                // Coloca a flag RecebidoEstoque em TRUE
                try {
                    plcConnectorEst.writeBit(9, 64, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoEstoque em TRUE
                } catch (Exception e) {
                    System.out.println("ERRO: Atualização da Flag RecebidoEstoque [DB9:64.0] para TRUE");
                }

                byte offset = (byte) (68 + (estoqueInfoClp.getPosicaoEstoque() - 1));

                try {
                    // Atualiza cor no CLP
                    plcConnectorEst.writeByte(9, offset, (byte) estoqueInfoClp.getCorGuardarEstoque());

                    // Persiste a posição com a cor adicionada na API
                    estoqueService.assignBlockColorByPosicaoFisica(
                        estoqueInfoClp.getPosicaoEstoque(), 
                        CorEstoque.fromValue(estoqueInfoClp.getCorGuardarEstoque())
                    );
                } catch (Exception e) {
                    System.out.println("ERRO: Na tentativa de adicionar no Estoque");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * (ocupadoEst == true | retornoEstoqueCheio == true) & iniciarGuardarEst == true:
     * baixa a flag IniciarGuardar [DB9:64.1] para FALSE.
     */
    private void tratarResetIniciarGuardar(PlcConnector plcConnectorEst) {
        if ((estoqueInfoClp.isOcupado() == true | estoqueInfoClp.isRetornoEstoqueCheio() == true) & estoqueInfoClp.isIniciarGuardarEst() == true) {
            if (appStateConfig.isReadOnly() == false) {
                // Coloca a flag IniciarGuardar em FALSE
                try {
                    plcConnectorEst.writeBit(9, 64, 1, Boolean.parseBoolean("FALSE")); // Coloca iniciarGuardarEst em FALSE
                } catch (Exception e) {
                    System.out.println(
                            "ERRO: Atualização da Flag IniciarGuardarEstoque [DB9:64.1] para FALSE");
                }
            }
        }
    }

    /**
     * pedirPosicaoEst == true & ocupadoEst == false:
     * ESTOQUE está livre e pede posição para guardar. Localiza a primeira posição livre
     * no Magazine (0-LIVRE 1-PRETO 2-VERMELHO 3-AZUL), grava em PosicaoGuardar [DB9:66]
     * e sobe a flag IniciarGuardar [DB9:64.1] para TRUE.
     */
    private void tratarPedirPosicaoGuardar(PlcConnector plcConnectorEst) {
        if (estoqueInfoClp.isPedirPosicaoEst() == true & estoqueInfoClp.isOcupado() == false) {
            // Rotina para verificar qual posição está disponível para guardar
            if (!appStateConfig.isReadOnly()) {
                Estoque primeiraPosicaoLivre = estoqueService.findByCorEstoque(CorEstoque.VAZIO).get(0);
                if (primeiraPosicaoLivre != null) {
                    try {
                        // Atualiza a variável PosicaoGuardar no CLP ESTOQUE
                        plcConnectorEst.writeInt(9, 66, primeiraPosicaoLivre.getPosicaoFisica());
                    } catch (Exception e) {
                        System.out.println("ERRO: Atualização da PosicaoGuardarEstoque [DB9:66]");
                    }

                    try {
                        // Coloca a flag IniciarGuardar em TRUE
                        plcConnectorEst.writeBit(9, 64, 1, Boolean.parseBoolean("TRUE")); // coloca IniciarGuardar em TRUE
                    } catch (Exception e) {
                        System.out.println("ERRO: Atualização da Flag IniciarGuardarEstoque [DB9:64.1]");
                    }
                } else {
                    System.out.println("ERRO: Nao existe posição livre.");
                }
            }
        }
    }
}
