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
import com.smart.appsa.exception.EsteiraDesativadaException;
import com.smart.appsa.model.clp.MontagemInfo;

import lombok.RequiredArgsConstructor;

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

@Service
@RequiredArgsConstructor
public class SmartService {
    private final PlcConnectionService plcConnectionService;
    private final AppStateConfig appStateConfig;
    private final MontagemInfo montagemInfo;
    
    public void enviarParaProducao(PedidoConfigDTO config, PedidoInfoDTO detalhes) {

        // Verifica se as esteiras estão rodando, senão lança exception e dá rollback em tudo até agora
        if (!montagemInfo.getSupervisorioEstoque().equals("LIVRE") ||
            !montagemInfo.getSupervisorioExpedicao().equals("LIVRE") ||
            !montagemInfo.getSupervisorioMontagem().equals("LIVRE") ||
            !montagemInfo.getSupervisorioProcesso().equals("LIVRE") ) {
            throw new EsteiraDesativadaException();
        }

        // 1. Converter o DTO para um bloco de bytes (byte[])
        byte[] buffer = converterParaBytes(detalhes);

        printHex(buffer);
        // 2. Obter a conexão única via seu Service
        PlcConnector connector = plcConnectionService.getConnection(config.getIpClp());

        if (connector == null) {
            throw new ClpComunicacaoException(config.getIpClp(), "conexão indisponível");
        }

        try {
            // 3. Escrever bloco de bytes no CLP (ex: a partir da DB19, offset 2)
            connector.writeBlock(9, 2, 60, buffer);
            System.out.println("Dados enviados para o CLP: " + config.getIpClp());
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

    // Printar bloco de bytes do Pedido no console
    public void printHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        System.out.println("--- BLOCO DE BYTES (HEXADECIMAL) ---");

        for (int i = 0; i < bytes.length; i++) {
            // Converte o byte para Hex e garante que tenha 2 dígitos (ex: 0A em vez de A)
            sb.append(String.format("%02X ", bytes[i]));

            // Opcional: Quebra de linha a cada 10 bytes para facilitar a leitura
            if ((i + 1) % 10 == 0) {
                sb.append("\n");
            }
        }

        System.out.println(sb.toString());
        System.out.println("------------------------------------");
    }

    // Envia comando para a Planta Smart iniciar a produção do Pedido
    public void iniciarExecucaoPedido(String ipClp) {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        if (plcConnector == null) {
            return;
        }

        try {

            // Inicializa as flags da estação ESTOQUE
            //plcConnector.connect();
            plcConnector.writeBit(9, 0, 0, Boolean.parseBoolean("FALSE"));
            plcConnector.writeBit(9, 64, 0, Boolean.parseBoolean("FALSE"));
            plcConnector.writeBit(9, 64, 1, Boolean.parseBoolean("FALSE"));
            plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE"));

            // plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE"));
            // Iniciar pedido
            System.out.println("SETAR FLAG INICIAR PEDIDO");
            plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("TRUE"));
    
            Thread.sleep(800);

            System.out.println("RESETAR FLAG INICIAR PEDIDO");
            plcConnector.writeBit(9, 62, 0, Boolean.parseBoolean("FALSE"));
        
        } catch (Exception ex) {

        }
    }

    public void enviarTampa(int tampa) {
        System.out.println("\n\nSELETOR DE TAMPAS INSTALADO NA BANCADA\n\n");
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
            System.out.println("Resposta do ESP32: " + body);

            // Verificação robusta
            if (body == null || body.get("status") == null) {
                System.out.println("Deu erro");
                return;
            }

            String status = body.get("status").toString();

            // Verificação flexível (ignora maiúsculas/minúsculas)
            if (!status.toLowerCase().contains("ok")) {
                System.out.println("Deu erro");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Deu erro");
            return;
        }
    }

    public boolean sendBlockBytesToClp(String ipClp, int db, int offset, byte[] dados, int size) {
        PlcConnector plcConnector = plcConnectionService.getConnection(ipClp);
        if (plcConnector == null) {
            System.out.println("Sem conexão com CLP " + ipClp);
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