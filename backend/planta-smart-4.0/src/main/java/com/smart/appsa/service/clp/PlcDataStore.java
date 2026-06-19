package com.smart.appsa.service.clp;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.model.enums.Estacao;

import lombok.AllArgsConstructor;

/**
 * Armazena o último {@code byte[]} bruto lido de cada estação.
 * Substitui os antigos campos {@code static dataClp1..4} do {@code ClpController} e
 * é o ponto único consultado pelos endpoints {@code /data} e {@code /smartstream}.
 */
@Component
@AllArgsConstructor
public class PlcDataStore {

    private final Map<Estacao, byte[]> dados = new EnumMap<>(Estacao.class);
    private final AppStateConfig appStateConfig;

    public void update(Estacao estacao, byte[] data) {
        dados.put(estacao, data);
    }

    public byte[] getRaw(Estacao estacao) {
        return dados.get(estacao);
    }

    /** Última leitura da estação como string hexadecimal ("AB CD ..."), ou {@code null}. */
    public String getHex(Estacao estacao) {
        return toHex(getRaw(estacao));
    }

    /**
     * Leitura do ESTOQUE acrescida dos 6 bytes de status usados pelo stream SSE
     * (statusEstoque/Processo/Montagem/Expedicao/Producao + flag pedidoEmCurso).
     */
    public byte[] getEstoqueComStatus() {
        byte[] base = getRaw(Estacao.ESTOQUE);
        if (base == null) {
            return null;
        }
        byte[] estendido = new byte[base.length + 6];
        System.arraycopy(base, 0, estendido, 0, base.length);
        estendido[estendido.length - 6] = appStateConfig.getStatusEstoque();
        estendido[estendido.length - 5] = appStateConfig.getStatusProcesso();
        estendido[estendido.length - 4] = appStateConfig.getStatusMontagem();
        estendido[estendido.length - 3] = appStateConfig.getStatusExpedicao();
        estendido[estendido.length - 2] = appStateConfig.getStatusProducao();
        estendido[estendido.length - 1] = (byte) (appStateConfig.isPedidoEmCurso() ? 1 : 0);
        return estendido;
    }

    public static String toHex(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
