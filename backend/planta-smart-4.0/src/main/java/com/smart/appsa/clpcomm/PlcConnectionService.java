package com.smart.appsa.clpcomm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlcConnectionService {
    private static final Map<String, PlcConnector> conexoes = new ConcurrentHashMap<>();

    public synchronized PlcConnector getConnection(String ip) {
        PlcConnector connector = conexoes.get(ip);
            if (connector == null) {
                log.info("===== NOVA CONEXÃO COM O CLP: {} =====", ip);
                connector = new PlcConnector(ip, 102);
                try {
                    connector.connect();
                    conexoes.put(ip, connector);
                    log.info("Conexão estabelecida com sucesso: {}", ip);
                } catch (Exception e) {
                    log.error("Erro ao conectar ao CLP {}: {}", ip, e.getMessage(), e);
                    return null;
                }
            }
            return connector;
    }

    public synchronized void disconnect(String ip) {
        PlcConnector connector = conexoes.get(ip);
            if (connector != null) {
                try {
                    connector.disconnect();
                    log.info("Conexão com {} encerrada.", ip);
                } catch (Exception e) {
                    log.error("Erro ao desconectar do CLP {}: {}", ip, e.getMessage(), e);
                }
                conexoes.remove(ip);
            }
    }

    public void closeAll() {
        log.info("===== ENCERRAR CONEXÕES COM OS CLPs =====");
            for (Map.Entry<String, PlcConnector> entry : conexoes.entrySet()) {
                String ip = entry.getKey();
                PlcConnector connector = entry.getValue();
                try {
                    if (connector != null) {
                        connector.disconnect();
                        log.info("Conexão com {} encerrada com sucesso.", ip);
                    }
                } catch (Exception e) {
                    log.error("Erro ao encerrar conexão com {}: {}", ip, e.getMessage());
                }
            }
            conexoes.clear();
    }

}
