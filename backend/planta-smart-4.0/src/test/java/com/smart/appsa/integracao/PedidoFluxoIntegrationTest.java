package com.smart.appsa.integracao;

import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.appsa.dto.clp.PedidoConfigDTO;
import com.smart.appsa.dto.clp.PedidoInfoDTO;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.EstoqueRepository;
import com.smart.appsa.repository.ExpedicaoRepository;
import com.smart.appsa.repository.PedidoRepository;
import com.smart.appsa.service.SmartService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidoFluxoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private BlocoRepository blocoRepository;
    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private ExpedicaoRepository expedicaoRepository;

    // Fronteira com o CLP: mockada para o fluxo não depender da bancada física.
    @MockitoBean
    private SmartService smartService;

    @BeforeEach
    void limparEPopular() {
        blocoRepository.deleteAll();
        pedidoRepository.deleteAll();
        estoqueRepository.deleteAll();
        expedicaoRepository.deleteAll();

        // Estoque com 4 posições AZUL disponíveis (suficiente para um pedido Duplo).
        for (int posicao = 1; posicao <= 4; posicao++) {
            estoqueRepository.save(Estoque.builder()
                .posicaoFisica(posicao)
                .corEstoque(CorEstoque.AZUL)
                .build());
        }
        // Magazine de expedição com 4 posições livres (op atual = 0).
        for (int posicao = 1; posicao <= 4; posicao++) {
            expedicaoRepository.save(Expedicao.builder()
                .posicaoFisica(posicao)
                .ordemDeProducaoAtual(0)
                .build());
        }
    }

    // JSON de um pedido Duplo (2 blocos AZUL, andares 1 e 2). Enums viajam pelo seu valor numérico.
    private String pedidoDuploJson(int ordemDeProducao) {
        return """
            {
              "ordemDeProducao": %d,
              "tipo": "2",
              "corTampa": "2",
              "blocos": [
                { "cor": "3", "andar": "1", "laminas": [] },
                { "cor": "3", "andar": "2", "laminas": [] }
              ]
            }
            """.formatted(ordemDeProducao);
    }

    @Test
    @DisplayName("Cria pedido Duplo, reserva 2 posições de estoque e coloca o pedido em produção")
    void deveEnviarPedidoDuploParaProducaoEReservarDoisBlocosDoEstoque() throws Exception {
        // Criação do pedido e persiste como pendente
        MvcResult criacao = mockMvc.perform(post("/api/pedidos")
                .contentType("application/json")
                .content(pedidoDuploJson(101)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("PENDENTE"))
            .andReturn();

        Long pedidoId = objectMapper.readTree(criacao.getResponse().getContentAsString())
            .get("id").asLong();

        // Criar pedido não consome estoque, as 4 posições AZUL continuam lá.
        assertEquals(4, estoqueRepository.findByCorEstoque(CorEstoque.AZUL).size());

        // Envia para produção e daí sim reserva os estoques
        mockMvc.perform(put("/api/pedidos/start-production/{id}", pedidoId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PRODUCAO"));

        // O envio ao CLP foi acionado exatamente uma vez (fronteira mockada).
        verify(smartService, times(1)).enviarParaProducao(any(PedidoConfigDTO.class), any(PedidoInfoDTO.class));

        // O pedido aparece na lista "Em Produção".
        mockMvc.perform(get("/api/pedidos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == " + pedidoId + ")].status", hasItem("PRODUCAO")));

        // Estado persistido: pedido em PRODUCAO, com expedição atribuída.
        Pedido persistido = pedidoRepository.findById(pedidoId).orElseThrow();
        assertEquals(StatusPedido.PRODUCAO, persistido.getStatus());
        assertNotNull(persistido.getExpedicao(), "Deveria ter recebido uma posição de expedição");

        // os 2 blocos ficaram vinculados a 2 posições
        // físicas distintas do estoque (a baixa física em si é responsabilidade do CLP).
        List<Bloco> blocosDoPedido = blocoRepository.findAll().stream()
            .filter(b -> b.getPedido().getId().equals(pedidoId))
            .toList();
        assertEquals(2, blocosDoPedido.size());
        assertTrue(blocosDoPedido.stream().allMatch(b -> b.getEstoque() != null),
            "Cada bloco deveria estar vinculado a uma posição de estoque");
        long posicoesReservadas = blocosDoPedido.stream()
            .map(b -> b.getEstoque().getId())
            .distinct()
            .count();
        assertEquals(2, posicoesReservadas, "Devem ser 2 posições de estoque distintas reservadas");
    }

    @Test
    @DisplayName("Envio para produção sem estoque suficiente falha (422) e não aciona o CLP")
    void deveFalharAoEnviarParaProducaoSemEstoqueSuficiente() throws Exception {
        // Deixa apenas 1 posição AZUL: insuficiente para um pedido Duplo.
        estoqueRepository.deleteAll();
        estoqueRepository.save(Estoque.builder()
            .posicaoFisica(1)
            .corEstoque(CorEstoque.AZUL)
            .build());

        MvcResult criacao = mockMvc.perform(post("/api/pedidos")
                .contentType("application/json")
                .content(pedidoDuploJson(202)))
            .andExpect(status().isCreated())
            .andReturn();
        Long pedidoId = objectMapper.readTree(criacao.getResponse().getContentAsString())
            .get("id").asLong();

        // 422 (BusinessException -> EstoqueInsuficienteException).
        mockMvc.perform(put("/api/pedidos/start-production/{id}", pedidoId))
            .andExpect(status().isUnprocessableEntity());

        // Nada foi enviado ao CLP e a transação deu rollback: pedido segue PENDENTE.
        verify(smartService, never()).enviarParaProducao(any(), any());
        assertEquals(StatusPedido.PENDENTE,
            pedidoRepository.findById(pedidoId).orElseThrow().getStatus());
    }
}
