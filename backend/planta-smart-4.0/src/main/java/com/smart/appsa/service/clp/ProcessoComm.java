package com.smart.appsa.service.clp;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.model.clp.ProcessoInfo;
import com.smart.appsa.service.clp.reader.PlcDataObserver;

import lombok.AllArgsConstructor;

// Handler da estação PROCESSO (CLP 2 / DB2).
//
// <p>Registrado como {@link PlcDataObserver} no {@code PlcReaderTask} da estação,
// recebe a cada ciclo o bloco bruto lido do CLP, atualiza o {@link ProcessoInfo}
// e reage às flags de operação (start/finish/cancel), sincronizando de volta a
// flag {@code RecebidoOP} e o status de produção em {@link AppStateConfig}.
//
// <p>Toda escrita no CLP é condicionada a {@code !appStateConfig.isReadOnly()}:
// em modo somente-leitura a aplicação observa, mas nunca escreve de volta.
@Service
@AllArgsConstructor
public class ProcessoComm implements PlcDataObserver {
    // Data Block da estação PROCESSO.
    private static final int DB_PROCESSO = 2;
    // Offset (byte) da palavra de StatusOP, onde fica a flag RecebidoOP no bit 0.
    private static final int OFFSET_STATUS_OP = 0;
    // Bit, dentro de {@link #OFFSET_STATUS_OP}, da flag RecebidoOP.
    private static final int BIT_RECEBIDO_OP = 0;

    private PlcConnectionService plcConnectionService;
    private ProcessoInfo processoInfo;
    private AppStateConfig appStateConfig;

    @Override
    public void onData(String ip, byte[] data) {
        processarLeitura(ip, data);
    }

    // Atualiza o estado da estação a partir do bloco bruto e aplica as regras de negócio.
    public void processarLeitura(String ip, byte[] dadosProcesso) {
        PlcConnector plcConnectorPro = plcConnectionService.getConnection(ip);
        if (plcConnectorPro == null) {
            return;
        }

        lerVariaveis(dadosProcesso);

        // Encadeamento das regras de negócio da estação PROCESSO.
        resetarRecebidoOp(plcConnectorPro);
        tratarInicioOperacao(plcConnectorPro);
        tratarFimOperacao(plcConnectorPro);
    }

    // Mapeia o bloco bruto do CLP PROCESSO para o {@link ProcessoInfo}.
    private void lerVariaveis(byte[] dadosProcesso) {
        processoInfo.setRecebidoOp((dadosProcesso[0] & 0x01) != 0);

        processoInfo.setNumeroOP(((dadosProcesso[2] & 0xFF) << 8) | (dadosProcesso[3] & 0xFF));
        processoInfo.setCancelOP((dadosProcesso[4] & 0x01) != 0);
        processoInfo.setFinishOP((dadosProcesso[4] & 0x02) != 0);
        processoInfo.setStartOP((dadosProcesso[4] & 0x04) != 0);

        processoInfo.setOcupado((dadosProcesso[6] & 0x01) != 0);
        processoInfo.setAguardando((dadosProcesso[6] & 0x02) != 0);
        processoInfo.setManual((dadosProcesso[6] & 0x04) != 0);
        processoInfo.setEmergencia((dadosProcesso[6] & 0x08) != 0);
    }

    // Nenhuma operação em andamento (start, finish e cancel todas em FALSE):
    // baixa a flag RecebidoOP para FALSE, deixando a estação pronta para a próxima OP.
    private void resetarRecebidoOp(PlcConnector plcConnectorPro) {
        if (!processoInfo.isStartOP() && !processoInfo.isFinishOP() && !processoInfo.isCancelOP()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorPro.writeBit(DB_PROCESSO, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, false);
                } catch (Exception ex) {
                }
            }
        }
    }

    // Início da operação (startOP == true e recebidoOp == false):
    // marca statusProcesso = 1 (se há pedido em curso) e confirma a recepção da OP
    // subindo a flag RecebidoOP para TRUE.
    private void tratarInicioOperacao(PlcConnector plcConnectorPro) {
        if (processoInfo.isStartOP() && !processoInfo.isRecebidoOp()) {
            if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso()) {
                appStateConfig.setStatusProcesso((byte) 1);
            }

            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorPro.writeBit(DB_PROCESSO, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Fim da operação (finishOP == true e recebidoOp == false):
    // confirma a recepção subindo RecebidoOP para TRUE e marca statusProcesso = 2
    // (se há pedido em curso).
    private void tratarFimOperacao(PlcConnector plcConnectorPro) {
        if (processoInfo.isFinishOP() && !processoInfo.isRecebidoOp()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorPro.writeBit(DB_PROCESSO, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso()) {
                    appStateConfig.setStatusProcesso((byte) 2);
                }
            }
        }
    }
}
