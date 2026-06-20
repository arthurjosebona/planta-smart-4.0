package com.smart.appsa.service.clp.reader;

/**
 * Observador notificado a cada leitura concluída de um CLP (padrão Observer).
 * Implementado pelos {@code CommService}s e pelo {@code PlcDataStore}.
 */
public interface PlcDataObserver {
    void onData(String ip, byte[] data);
}
