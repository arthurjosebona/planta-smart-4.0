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
public class MontagemCommService implements PlcDataObserver {
    private PlcConnectionService plcConnectionService;
    private MontagemInfo montagemInfo;
    private AppStateConfig appStateConfig;

    @Override
    public void onData(String ip, byte[] data) {
        processData(ip, data);
    }

    public void processData(String ip, byte[] dadosClp3) {
        // lógica que hoje está no método clpMontagem(...)

        //-------------- Apresentação no console -----------------
        StringBuilder leituraClp3 = new StringBuilder();
        for (byte b : dadosClp3) {
            leituraClp3.append(String.format("%02X ", b));
        }
        String clp3 = leituraClp3.toString().trim();
        //System.out.println("[CLP3] (" + dadosClp3.length + " bytes): " + clp3);

        PlcConnector plcConnectorMon = plcConnectionService.getConnection(ip);
        if (plcConnectorMon == null) {
            return;
        }
        //-------------- Leitura das variáveis -------------------
        montagemInfo.setRecebidoOp((dadosClp3[0] & 0x01) != 0);

        montagemInfo.setNumeroOP(((dadosClp3[2] & 0xFF) << 8) | (dadosClp3[3] & 0xFF));
        montagemInfo.setCancelOP((dadosClp3[4] & 0x01) != 0);
        montagemInfo.setFinishOP((dadosClp3[4] & 0x02) != 0);
        montagemInfo.setStartOP((dadosClp3[4] & 0x04) != 0);

        montagemInfo.setOcupado((dadosClp3[6] & 0x01) != 0);
        montagemInfo.setAguardando((dadosClp3[6] & 0x02) != 0);
        montagemInfo.setManual((dadosClp3[6] & 0x04) != 0);
        montagemInfo.setEmergencia((dadosClp3[6] & 0x08) != 0);

        // System.out.println("StatusEstoque: " + statusEstoque + "\n"
        //         + "StatusProcesso: " + statusProcesso + "\n"
        //         + "StatusMontagem: " + statusMontagem + "\n"
        //         + "StatusExpedicao: " + statusExpedicao + "\n");
        // Se as três flags (StartOPMon, FinishOPMon e CancelOPMon) estão em FALSE, então a flag
        // RecebidoOPMon fica em FALSE
        if (montagemInfo.isStartOP() == false && montagemInfo.isFinishOP() == false && montagemInfo.isCancelOP() == false) {
            if (appStateConfig.isReadOnly() == false) {

                try {

                    plcConnectorMon.writeBit(57, 0, 0, Boolean.parseBoolean("FALSE")); // coloca RecebidoOPMon em FALSE
                } catch (Exception ex) {
                }
            }
        }
        // Se a estação MONTAGEM sinalizou o inicio da operação e recebidoOpMon está em FALSE, então a
        // flag RecebidoOPMon fica em TRUE
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

        // Se a estação MONTAGEM sinalizou o témino da operação e ficou OCUPADO, então a
        // flag RecebidoOP fica em TRUE
        if (montagemInfo.isFinishOP() == true && montagemInfo.isRecebidoOp() == false) {
            if (appStateConfig.isReadOnly() == false) {

                try {
                    plcConnectorMon.writeBit(57, 0, 0, Boolean.parseBoolean("TRUE"));  // coloca RecebidoOPMon em TRUE
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
