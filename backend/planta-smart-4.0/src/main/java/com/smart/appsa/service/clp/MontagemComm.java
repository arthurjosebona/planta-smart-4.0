package com.smart.appsa.service.clp;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.model.clp.MontagemInfoClp;
import com.smart.appsa.service.PedidoService;
import com.smart.appsa.service.clp.reader.PlcDataObserver;

import lombok.AllArgsConstructor;

// Handler da estação MONTAGEM (CLP 3 / DB57).
//
// <p>Registrado como {@link PlcDataObserver} no {@code PlcReaderTask} da estação,
// recebe a cada ciclo o bloco bruto lido do CLP, atualiza o {@link MontagemInfo}
// e reage às flags de operação (start/finish/cancel), sincronizando de volta a
// flag {@code RecebidoOP} e o status de produção em {@link AppStateConfig}.
//
// <p>Toda escrita no CLP é condicionada a {@code !appStateConfig.isReadOnly()}:
// em modo somente-leitura a aplicação observa, mas nunca escreve de volta.
@Service
@AllArgsConstructor
public class MontagemComm implements PlcDataObserver {
    // Data Block da estação MONTAGEM.
    private static final int DB_MONTAGEM = 57;
    // Offset (byte) da palavra de StatusOP, onde fica a flag RecebidoOP no bit 0.
    private static final int OFFSET_STATUS_OP = 0;
    // Bit, dentro de {@link #OFFSET_STATUS_OP}, da flag RecebidoOP.
    private static final int BIT_RECEBIDO_OP = 0;

    private PlcConnectionService plcConnectionService;
    private MontagemInfoClp montagemInfo;
    private AppStateConfig appStateConfig;
    private PedidoService pedidoService;

    @Override
    public void onData(String ip, byte[] data) {
        processarLeitura(ip, data);
    }

    // Atualiza o estado da estação a partir do bloco bruto e aplica as regras de negócio.
    public void processarLeitura(String ip, byte[] dadosMontagem) {
        PlcConnector plcConnectorMon = plcConnectionService.getConnection(ip);
        if (plcConnectorMon == null) {
            return;
        }

        lerVariaveis(dadosMontagem);

        // Encadeamento das regras de negócio da estação MONTAGEM.
        resetarRecebidoOp(plcConnectorMon);
        tratarInicioOperacao(plcConnectorMon);
        tratarFimOperacao(plcConnectorMon);
    }

    // Mapeia o bloco bruto do CLP MONTAGEM para o {@link MontagemInfo}.
    private void lerVariaveis(byte[] dadosMontagem) {
        montagemInfo.setRecebidoOp((dadosMontagem[0] & 0x01) != 0);

        montagemInfo.setNumeroOP(((dadosMontagem[2] & 0xFF) << 8) | (dadosMontagem[3] & 0xFF));
        montagemInfo.setCancelOP((dadosMontagem[4] & 0x01) != 0);
        montagemInfo.setFinishOP((dadosMontagem[4] & 0x02) != 0);
        montagemInfo.setStartOP((dadosMontagem[4] & 0x04) != 0);

        montagemInfo.setOcupado((dadosMontagem[6] & 0x01) != 0);
        montagemInfo.setAguardando((dadosMontagem[6] & 0x02) != 0);
        montagemInfo.setManual((dadosMontagem[6] & 0x04) != 0);
        montagemInfo.setEmergencia((dadosMontagem[6] & 0x08) != 0);

        lerProvisorioBancadas(dadosMontagem);
    }

    private void lerProvisorioBancadas(byte[] dadosMontagem) {
        // Cada bloco DB de bancada tem 16 bytes, (tamanho total + conteúdo real + 14 chars)
        byte[] blocoDb30  = java.util.Arrays.copyOfRange(dadosMontagem, 9, 25);
        byte[] blocoDb600 = java.util.Arrays.copyOfRange(dadosMontagem, 25, 41);
        byte[] blocoDb92  = java.util.Arrays.copyOfRange(dadosMontagem, 41, 57);
        byte[] blocoDb60  = java.util.Arrays.copyOfRange(dadosMontagem, 57, 73);
        
        montagemInfo.setSupervisorioEstoque(lerStringS7(blocoDb30));
        montagemInfo.setSupervisorioProcesso(lerStringS7(blocoDb600));
        montagemInfo.setSupervisorioMontagem(lerStringS7(blocoDb92));
        montagemInfo.setSupervisorioExpedicao(lerStringS7(blocoDb60));
    }

    // Decodifica um bloco STRING no formato S7 (Siemens): 
    // byte[0] = tamanho máximo declarado, byte[1] = tamanho atual (length),
    // byte[2..] = caracteres ASCII válidos até 'length'.
    private String lerStringS7(byte[] dados) {
        if (dados == null || dados.length < 2) {
            return "";
        }

        int maxLen = dados[0] & 0xFF;
        int curLen = dados[1] & 0xFF;

        // Proteção contra dados corrompidos ou tamanho maior que o buffer disponível
        int disponivel = dados.length - 2;
        int tamanhoReal = Math.min(curLen, disponivel);
        tamanhoReal = Math.min(tamanhoReal, maxLen);

        if (tamanhoReal <= 0) {
            return "";
        }

        return new String(dados, 2, tamanhoReal, java.nio.charset.StandardCharsets.US_ASCII).trim();
    }

    // Nenhuma operação em andamento (start, finish e cancel todas em FALSE):
    // baixa a flag RecebidoOP para FALSE, deixando a estação pronta para a próxima OP.
    private void resetarRecebidoOp(PlcConnector plcConnectorMon) {
        if (!montagemInfo.isStartOP() && !montagemInfo.isFinishOP() && !montagemInfo.isCancelOP()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorMon.writeBit(DB_MONTAGEM, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, false);
                } catch (Exception ex) {
                }
            }
        }
    }

    // Início da operação (startOP == true e recebidoOp == false):
    // marca statusMontagem = 1 (se há pedido em curso) e confirma a recepção da OP
    // subindo a flag RecebidoOP para TRUE.
    private void tratarInicioOperacao(PlcConnector plcConnectorMon) {
        if (montagemInfo.isStartOP() && !montagemInfo.isRecebidoOp()) {
            if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso()) {
                appStateConfig.setStatusMontagem((byte) 1);
            }

            pedidoService.handleEntradaMontagem(montagemInfo.getNumeroOP());
            
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorMon.writeBit(DB_MONTAGEM, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                } catch (Exception ex) {
                }
            }
        }
    }

    // Fim da operação (finishOP == true e recebidoOp == false):
    // confirma a recepção subindo RecebidoOP para TRUE e marca statusMontagem = 2
    // (se há pedido em curso).
    private void tratarFimOperacao(PlcConnector plcConnectorMon) {
        if (montagemInfo.isFinishOP() && !montagemInfo.isRecebidoOp()) {
            if (!appStateConfig.isReadOnly()) {
                try {
                    plcConnectorMon.writeBit(DB_MONTAGEM, OFFSET_STATUS_OP, BIT_RECEBIDO_OP, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (appStateConfig.getStatusProducao() == 0 & appStateConfig.isPedidoEmCurso()) {
                    appStateConfig.setStatusMontagem((byte) 2);
                }
            }
        }
    }
}
