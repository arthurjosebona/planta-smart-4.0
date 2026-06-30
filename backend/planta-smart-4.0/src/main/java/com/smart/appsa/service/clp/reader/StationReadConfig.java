package com.smart.appsa.service.clp.reader;

import java.util.List;
import java.util.Map;

import com.smart.appsa.model.enums.Estacao;

// Configuração de leitura de uma estação: quais Data Blocks ler e com qual intervalo.
// Centraliza os parâmetros que antes estavam espalhados no {@code switch} do controller.
public record StationReadConfig(Estacao estacao, long delayMs, List<PlcReadRequest> reads) {

    // Configurações padrão de leitura de cada estação.
    public static Map<Estacao, StationReadConfig> padroes() {
        return Map.of(
                Estacao.ESTOQUE, new StationReadConfig(Estacao.ESTOQUE, 400, List.of(
                        new PlcReadRequest(9, 0, 111),
                        new PlcReadRequest(6, 0, 60))),
                Estacao.PROCESSO, new StationReadConfig(Estacao.PROCESSO, 600, List.of(
                        new PlcReadRequest(2, 0, 9))),
                Estacao.MONTAGEM, new StationReadConfig(Estacao.MONTAGEM, 600, List.of(
                        new PlcReadRequest(57, 0, 9),
                        new PlcReadRequest(30, 16, 16),
                        new PlcReadRequest(600, 14, 16),
                        new PlcReadRequest(92, 2, 16),
                        new PlcReadRequest(60, 20, 16))),
                Estacao.EXPEDICAO, new StationReadConfig(Estacao.EXPEDICAO, 400, List.of(
                        new PlcReadRequest(9, 0, 48))));
    }
}
