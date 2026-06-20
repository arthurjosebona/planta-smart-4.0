package com.smart.appsa.service.clp;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.model.clp.ProcessoInfo;
import com.smart.appsa.service.clp.reader.PlcDataObserver;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class ProcessoComm implements PlcDataObserver {
    private PlcConnectionService plcConnectionService;
    private ProcessoInfo processoInfo;
    private AppStateConfig appStateConfig;

    @Override
    public void onData(String ip, byte[] data) {
        processData(ip, data);
    }

    public void processData(String ip, byte[] dadosClp2) {
        logLeitura(dadosClp2);

        PlcConnector plcConnectorPro = plcConnectionService.getConnection(ip);
        if (plcConnectorPro == null) {
            return;
        }

        lerVariaveis(dadosClp2);

        // Regras de negócio da estação PROCESSO
        tratarResetRecebidoOp(plcConnectorPro);
        tratarStartOp(plcConnectorPro);
        tratarFinishOp(plcConnectorPro);
    }

    /** Apresentação no console da leitura bruta (em hexadecimal). */
    private void logLeitura(byte[] dadosClp2) {
        StringBuilder leituraClp2 = new StringBuilder();
        for (byte b : dadosClp2) {
            leituraClp2.append(String.format("%02X ", b));
        }
    }

    /** Lê as variáveis do bloco bruto do CLP PROCESSO para {@link ProcessoInfo}. */
    private void lerVariaveis(byte[] dadosClp2) {
        processoInfo.setRecebidoOp((dadosClp2[0] & 0x01) != 0);

        processoInfo.setNumeroOP(((dadosClp2[2] & 0xFF) << 8) | (dadosClp2[3] & 0xFF));
        processoInfo.setCancelOP((dadosClp2[4] & 0x01) != 0);
        processoInfo.setFinishOP((dadosClp2[4] & 0x02) != 0);
        processoInfo.setStartOP((dadosClp2[4] & 0x04) != 0);

        processoInfo.setOcupado((dadosClp2[6] & 0x01) != 0);
        processoInfo.setAguardando((dadosClp2[6] & 0x02) != 0);
        processoInfo.setManual((dadosClp2[6] & 0x04) != 0);
        processoInfo.setEmergencia((dadosClp2[6] & 0x08) != 0);
    }

    /**
     * StartOP, FinishOP e CancelOP todas em FALSE:
     * baixa a flag RecebidoOP [DB2:0.0] para FALSE.
     */
    private void tratarResetRecebidoOp(PlcConnector plcConnectorPro) {
        if (processoInfo.isStartOP() == false && processoInfo.isFinishOP() == false && processoInfo.isCancelOP() == false) {
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorPro.writeBit(2, 0, 0, Boolean.parseBoolean("FALSE")); // coloca RecebidoOPPro em FALSE
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * startOP == true & recebidoOp == false:
     * PROCESSO sinalizou o início da operação -> statusProcesso = 1 (se há pedido em curso)
     * e sobe a flag RecebidoOP [DB2:0.0] para TRUE.
     */
    private void tratarStartOp(PlcConnector plcConnectorPro) {
        if (processoInfo.isStartOP() == true && processoInfo.isRecebidoOp() == false) {
            if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso() == true) {
                appStateConfig.setStatusProcesso((byte) 1);
            } else {
                //statusProcesso = 0;
            }

            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorPro.writeBit(2, 0, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoOPPro em TRUE
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * finishOP == true & recebidoOp == false:
     * PROCESSO sinalizou o término da operação -> sobe a flag RecebidoOP [DB2:0.0] para
     * TRUE e marca statusProcesso = 2 (se há pedido em curso).
     */
    private void tratarFinishOp(PlcConnector plcConnectorPro) {
        if (processoInfo.isFinishOP() == true && processoInfo.isRecebidoOp() == false) {
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorPro.writeBit(2, 0, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoOPPro em TRUE
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso() == true) {
                    appStateConfig.setStatusProcesso((byte) 2);
                } else {
                    //statusProcesso = 0;
                }
            }
        }
    }
}
