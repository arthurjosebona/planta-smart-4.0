package com.smart.appsa.service.clp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.model.clp.ProcessoInfo;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class ProcessoCommService {
    private PlcConnectionService plcConnectionService;
    private ProcessoInfo processoInfo;

    public void processData(String ip, byte[] dadosClp2) {
        // lógica que hoje está no método clpProcesso(...)

        //-------------- Apresentação no console -----------------
        StringBuilder leituraClp2 = new StringBuilder();
        for (byte b : dadosClp2) {
            leituraClp2.append(String.format("%02X ", b));
        }
        String clp2 = leituraClp2.toString().trim();
        //System.out.println("[CLP2] " + clp2);

        PlcConnector plcConnectorPro = plcConnectionService.getConnection(ip);
        if (plcConnectorPro == null) {
            return;
        }

        //-------------- Leitura das variáveis -------------------
        processoInfo.setRecebidoOp((dadosClp2[0] & 0x01) != 0);

        processoInfo.setNumeroOP(((dadosClp2[2] & 0xFF) << 8) | (dadosClp2[3] & 0xFF));
        processoInfo.setCancelOP((dadosClp2[4] & 0x01) != 0);
        processoInfo.setFinishOP((dadosClp2[4] & 0x02) != 0);
        processoInfo.setStartOP((dadosClp2[4] & 0x04) != 0);

        processoInfo.setOcupado((dadosClp2[6] & 0x01) != 0);
        processoInfo.setAguardando((dadosClp2[6] & 0x02) != 0);
        processoInfo.setManual((dadosClp2[6] & 0x04) != 0);
        processoInfo.setEmergencia((dadosClp2[6] & 0x08) != 0);

        // System.out.println("StatusEstoque: " + statusEstoque + "\n"
        //         + "StatusProcesso: " + statusProcesso + "\n"
        //         + "StatusMontagem: " + statusMontagem + "\n"
        //         + "StatusExpedicao: " + statusExpedicao + "\n");
        // Se as três flags (StartOP, FinishOP e CancelOP) estão em FALSE, então a flag
        // RecebidoOP fica em FALSE
        if (processoInfo.isStartOP() == false && processoInfo.isFinishOP() == false && processoInfo.isCancelOP() == false) {
            if (SmartService.readOnly == false) {

                try {
                    plcConnectorPro.writeBit(2, 0, 0, Boolean.parseBoolean("FALSE")); // coloca RecebidoOPPro em FALSE
                } catch (Exception ex) {
                }
            }
        }
        // Se a estação PROCESSO sinalizou o inicio da operação e recebidoOpPro está em FALSE, então a
        // flag RecebidoOPPRO fica em TRUE
        if (processoInfo.isStartOP() == true && processoInfo.isRecebidoOp() == false) {
            if (SmartService.statusProducao == 0 & SmartService.pedidoEmCurso == true) {
                SmartService.statusProcesso = 1;
            } else {
                //statusProcesso = 0;
            }

            if (SmartService.readOnly == false) {
                try {
                    plcConnectorPro.writeBit(2, 0, 0, Boolean.parseBoolean("TRUE"));   // coloca RecebidoOPPro em TRUE
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        }
        // Se a estação PROCESSO sinalizou o témino da operação e ficou OCUPADO, então a
        // flag RecebidoOP fica em TRUE
        if (processoInfo.isFinishOP() == true && processoInfo.isRecebidoOp() == false) {
            if (SmartService.readOnly == false) {

                try {
                    plcConnectorPro.writeBit(2, 0, 0, Boolean.parseBoolean("TRUE"));  // coloca RecebidoOPPro em TRUE
                } catch (Exception e) {

                    e.printStackTrace();
                }
                if (SmartService.statusProducao == 0 & SmartService.pedidoEmCurso == true) {
                    SmartService.statusProcesso = 2;
                } else {
                    //statusProcesso = 0;
                }

            }

        }



        
    }


}
