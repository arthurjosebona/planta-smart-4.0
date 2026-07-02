package com.smart.appsa.service;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.dto.clp.PedidoConfigDTO;
import com.smart.appsa.dto.clp.PedidoInfoDTO;
import com.smart.appsa.exception.ClpComunicacaoException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartService {
    private final PlcConnectionService plcConnectionService;
    private final AppStateConfig appStateConfig;
    private final ApplicationEventPublisher eventPublisher;
    
    public void enviarParaProducao(PedidoConfigDTO config, PedidoInfoDTO detalhes) {
        byte[] buffer = converterParaBytes(detalhes);
        log.debug("Bloco de bytes gerado para CLP {}:\n{}", config.getIpClp(), formatHex(buffer));

        PlcConnector connector = plcConnectionService.getConnection(config.getIpClp());
        if (connector == null) {
            throw new ClpComunicacaoException(config.getIpClp(), "conexão indisponível");
        }

        try {
            connector.writeBlock(9, 2, 60, buffer);
            log.info("Bloco de dados enviado ao CLP {} (DB9, offset 2, 60 bytes).", config.getIpClp());
            enviarTampa(config.getTampaPedido());
            iniciarExecucaoPedido(config.getIpClp());
        } catch (Exception ex) {
            log.error("Falha ao enviar dados para o CLP {}: {}", config.getIpClp(), ex.getMessage(), ex);
            throw new ClpComunicacaoException(config.getIpClp(), ex);
        }
    }

    // Converter PedidoInfoDTO para bloco de bytes a ser enviado para o Clp_Estoque
    private byte[] converterParaBytes(PedidoInfoDTO dto) {
        // Pega todos os campos da classe
        Field[] campos = dto.getClass().getDeclaredFields();

        // Cada int será um Short (2 bytes) no CLP. 
        // Tamanho = número de campos * 2
        ByteBuffer buffer = ByteBuffer.allocate(campos.length * 2);

        try {
            for (Field campo : campos) {
                campo.setAccessible(true); // Permite ler o campo private

                // Pega o valor int do campo no objeto dto
                int valor = campo.getInt(dto);

                // Coloca no buffer como Short (2 bytes)
                buffer.putShort((short) valor);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return buffer.array();
    }

    private String formatHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- BLOCO DE BYTES (HEX) ---\n");
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X ", bytes[i]));
            if ((i + 1) % 10 == 0) sb.append("\n");
        }
        sb.append("\n----------------------------");
        return sb.toString();
    }

    // Envia comando para a Planta Smart iniciar a produção do Pedido
    public void iniciarExecucaoPedido(String ipClp) {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        if (plcConnector == null) {
            log.warn("iniciarExecucaoPedido: sem conexão com CLP {}", ipClp);
            return;
        }

        try {
            log.debug("Resetando flags do estoque no CLP {}...", ipClp);
            plcConnector.writeBit(9, 0, 0, false);
            plcConnector.writeBit(9, 64, 0, false);
            plcConnector.writeBit(9, 64, 1, false);
            plcConnector.writeBit(9, 62, 0, false);

            log.info("Setando flag IniciarPedido [DB9:62.0] = TRUE no CLP {}", ipClp);
            plcConnector.writeBit(9, 62, 0, true);
        } catch (Exception ex) {
            log.error("Erro ao setar flag IniciarPedido no CLP {}: {}", ipClp, ex.getMessage(), ex);
        }
    }

    public void enviarTampa(int tampa) {
        log.info("Enviando tampa {} para o seletor ESP32.", tampa);
        try {
            RestTemplate apiSeletorTampa = new RestTemplate();
            String url = "http://10.74.241.245/api/move_pos";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("pos", String.valueOf(tampa));
            map.add("offset", "0");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<Map<String, Object>> response = apiSeletorTampa.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            log.debug("Resposta do ESP32 (tampa {}): {}", tampa, body);

            if (body == null || body.get("status") == null) {
                log.error("ESP32 retornou resposta inválida para tampa {}: body={}", tampa, body);
                return;
            }

            String status = body.get("status").toString();
            if (!status.toLowerCase().contains("ok")) {
                log.error("ESP32 rejeitou o comando de tampa {}: status={}", tampa, status);
                return;
            }

            log.info("Tampa {} confirmada pelo ESP32.", tampa);
        } catch (Exception e) {
            log.error("Falha ao comunicar com ESP32 para tampa {}: {}", tampa, e.getMessage(), e);
        }
    }

    public boolean sendBlockBytesToClp(String ipClp, int db, int offset, byte[] dados, int size) {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        if (plcConnector == null) {
            log.warn("sendBlockBytesToClp: sem conexão com CLP {}", ipClp);
            return false;
        }
        if (!appStateConfig.isReadOnly()) {
            try {
                plcConnector.writeBlock(db, offset, size, dados); // escreve no bloco de dados
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        // se for readOnly ou nada a fazer, considere sucesso
        return true;
    }
}