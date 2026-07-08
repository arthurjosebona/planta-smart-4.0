package com.smart.appsa.service;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.dto.clp.PedidoConfigDTO;
import com.smart.appsa.dto.clp.PedidoInfoDTO;
import com.smart.appsa.events.UpdateExpedicaoEvent;
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
        // 1. Converter o DTO para um bloco de bytes (byte[])
        byte[] buffer = converterParaBytes(detalhes);

        printHex(buffer);
        // 2. Obter a conexão única via seu Service
        PlcConnector connector = plcConnectionService.getConnection(config.getIpClp());

        if (connector == null) {
            throw new ClpComunicacaoException(config.getIpClp(), "conexão indisponível");
        }

        // eventPublisher.publishEvent(new UpdateExpedicaoEvent(this, detalhes.getPosicaoExpedicao(), detalhes.getNumeroPedido()));

        try {
            log.info("SMART: escrevendo bloco pedido DB9:2 (60 bytes) no CLP {}", config.getIpClp());
            connector.writeBlock(9, 2, 60, buffer);
            log.info("SMART: dados do pedido enviados para o CLP {}", config.getIpClp());
            enviarTampa(config.getTampaPedido());
            iniciarExecucaoPedido(config.getIpClp());

        } catch (Exception ex) {
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

    private void printHex(byte[] bytes) {
        if (!log.isDebugEnabled()) return;
        StringBuilder sb = new StringBuilder("--- BLOCO DE BYTES (HEXADECIMAL) ---\n");
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("%02X ", bytes[i]));
            if ((i + 1) % 10 == 0) sb.append("\n");
        }
        sb.append("\n------------------------------------");
        log.debug("SMART: {}", sb);
    }

    // Envia comando para a Planta Smart iniciar a produção do Pedido
    public void iniciarExecucaoPedido(String ipClp) {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        if (plcConnector == null) {
            return;
        }

        try {
            log.debug("SMART: inicializando flags da estação ESTOQUE no CLP {}", ipClp);
            plcConnector.writeBit(9, 0, 0, false);
            plcConnector.writeBit(9, 64, 0, false);
            plcConnector.writeBit(9, 64, 1, false);
            plcConnector.writeBit(9, 62, 0, false);

            log.info("SMART: setando flag IniciarPedido [DB9:62.0] = true no CLP {}", ipClp);
            plcConnector.writeBit(9, 62, 0, true);

            Thread.sleep(800);

            log.info("SMART: resetando flag IniciarPedido [DB9:62.0] = false no CLP {}", ipClp);
            plcConnector.writeBit(9, 62, 0, false);

        } catch (Exception ex) {
            log.error("SMART: erro ao iniciar execução do pedido no CLP {}: {}", ipClp, ex.getMessage());
        }
    }

    public void enviarTampa(int tampa) {
        log.info("SMART: enviando comando de tampa {} ao ESP32", tampa);
        // Passo 2) Selecionar a tampa via POST
        try {
            RestTemplate apiSeletorTampa = new RestTemplate();
            String url = "http://10.74.241.245/api/move_pos";

            // 1. Definir o cabeçalho como application/x-www-form-urlencoded
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 2. Usar MultiValueMap (específico para formulários no Spring)
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("pos", String.valueOf(tampa));
            map.add("offset", "0");

            // 3. Criar a entidade com cabeçalhos e corpo
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            // 4. Tente ler a resposta primeiro como String para ver o que o ESP32 está
            // realmente enviando
            ResponseEntity<Map<String, Object>> response = apiSeletorTampa.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            log.debug("SMART: resposta do ESP32: {}", body);

            if (body == null || body.get("status") == null) {
                log.error("SMART: ESP32 retornou resposta inválida para tampa {}", tampa);
                return;
            }

            String status = body.get("status").toString();

            if (!status.toLowerCase().contains("ok")) {
                log.error("SMART: ESP32 retornou status de erro '{}' para tampa {}", status, tampa);
                return;
            }

        } catch (Exception e) {
            log.error("SMART: erro ao enviar comando de tampa {} ao ESP32: {}", tampa, e.getMessage(), e);
        }
    }

    public boolean sendBlockBytesToClp(String ipClp, int db, int offset, byte[] dados, int size) {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        if (plcConnector == null) {
            log.warn("SMART: sem conexão com CLP {} para escrita em DB{}:{}", ipClp, db, offset);
            return false;
        }
        if (!appStateConfig.isReadOnly()) {
            try {
                log.info("SMART: escrevendo {} bytes em DB{}:{} no CLP {}", size, db, offset, ipClp);
                plcConnector.writeBlock(db, offset, size, dados);
                return true;
            } catch (Exception e) {
                log.error("SMART: erro ao escrever em DB{}:{} no CLP {}: {}", db, offset, ipClp, e.getMessage(), e);
                return false;
            }
        }
        // se for readOnly ou nada a fazer, considere sucesso
        return true;
    }
}