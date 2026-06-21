package com.smart.appsa.service.clp;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.model.clp.MontagemInfo;
import com.smart.appsa.service.clp.reader.PlcDataObserver;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MontagemComm implements PlcDataObserver {
    private PlcConnectionService plcConnectionService;
    private MontagemInfo montagemInfo;
    private AppStateConfig appStateConfig;

    @Override
    public void onData(String ip, byte[] data) {
        processData(ip, data);
    }

    public void processData(String ip, byte[] dadosClp3) {
        logLeitura(dadosClp3);

        PlcConnector plcConnectorMon = plcConnectionService.getConnection(ip);
        if (plcConnectorMon == null) {
            return;
        }

        lerVariaveis(dadosClp3);

        // Regras de negócio da estação MONTAGEM
        tratarResetRecebidoOp(plcConnectorMon);
        tratarStartOp(plcConnectorMon);
        tratarFinishOp(plcConnectorMon);
    }

    /** Apresentação no console da leitura bruta (em hexadecimal). */
    private void logLeitura(byte[] dadosClp3) {
        StringBuilder leituraClp3 = new StringBuilder();
        for (byte b : dadosClp3) {
            leituraClp3.append(String.format("%02X ", b));
        }
    }

    /** Lê as variáveis do bloco bruto do CLP MONTAGEM para {@link MontagemInfo}. */
    private void lerVariaveis(byte[] dadosClp3) {
        montagemInfo.setRecebidoOp((dadosClp3[0] & 0x01) != 0);

        montagemInfo.setNumeroOP(((dadosClp3[2] & 0xFF) << 8) | (dadosClp3[3] & 0xFF));
        montagemInfo.setCancelOP((dadosClp3[4] & 0x01) != 0);
        montagemInfo.setFinishOP((dadosClp3[4] & 0x02) != 0);
        montagemInfo.setStartOP((dadosClp3[4] & 0x04) != 0);

        montagemInfo.setOcupado((dadosClp3[6] & 0x01) != 0);
        montagemInfo.setAguardando((dadosClp3[6] & 0x02) != 0);
        montagemInfo.setManual((dadosClp3[6] & 0x04) != 0);
        montagemInfo.setEmergencia((dadosClp3[6] & 0x08) != 0);
    }

    /**
     * StartOPMon, FinishOPMon e CancelOPMon todas em FALSE:
     * baixa a flag RecebidoOPMon [DB57:0.0] para FALSE.
     */
    private void tratarResetRecebidoOp(PlcConnector plcConnectorMon) {
        if (montagemInfo.isStartOP() == false && montagemInfo.isFinishOP() == false && montagemInfo.isCancelOP() == false) {
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorMon.writeBit(57, 0, 0, Boolean.parseBoolean("FALSE")); // coloca RecebidoOPMon em FALSE
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * startOP == true & recebidoOp == false:
     * MONTAGEM sinalizou o início da operação -> statusMontagem = 1 (se há pedido em curso)
     * e sobe a flag RecebidoOPMon [DB57:0.0] para TRUE.
     */
    private void tratarStartOp(PlcConnector plcConnectorMon) {
        if (montagemInfo.isStartOP() == true && montagemInfo.isRecebidoOp() == false) {
            if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso() == true) {
                appStateConfig.setStatusMontagem((byte) 1);
            } else {
                //statusMontagem = 0;
            }

            // updateDisplayStation();
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorMon.writeBit(57, 0, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoOPMon em TRUE
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * finishOP == true & recebidoOp == false:
     * MONTAGEM sinalizou o término da operação -> sobe a flag RecebidoOPMon [DB57:0.0]
     * para TRUE e marca statusMontagem = 2 (se há pedido em curso).
     */
    private void tratarFinishOp(PlcConnector plcConnectorMon) {
        if (montagemInfo.isFinishOP() == true && montagemInfo.isRecebidoOp() == false) {
            if (appStateConfig.isReadOnly() == false) {
                try {
                    plcConnectorMon.writeBit(57, 0, 0, Boolean.parseBoolean("TRUE")); // coloca RecebidoOPMon em TRUE
                } catch (Exception e) {
                    e.printStackTrace();
                } // RecebidoOPMon
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso() == true) {
                    appStateConfig.setStatusMontagem((byte) 2);
                } else {
                    //statusMontagem = 0;
                }
            }
        }
    }
}
