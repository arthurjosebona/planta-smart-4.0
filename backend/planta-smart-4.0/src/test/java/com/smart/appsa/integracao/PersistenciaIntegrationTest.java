package com.smart.appsa.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;
import com.smart.appsa.repository.BlocoRepository;
import com.smart.appsa.repository.PedidoRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersistenciaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private BlocoRepository blocoRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void limpar() {
        blocoRepository.deleteAll();
        pedidoRepository.deleteAll();
    }

    @Test
    @DisplayName("Pedido cadastrado sobrevive ao descarte do estado em memória (lido de volta do banco)")
    void dadosCadastradosPermanecemAposDescartarContextoDePersistencia() throws Exception {
        String pedidoJson = """
            {
              "ordemDeProducao": 777,
              "tipo": "1",
              "corTampa": "2",
              "blocos": [
                { "cor": "3", "andar": "1", "laminas": [] }
              ]
            }
            """;

        MvcResult resultado = mockMvc.perform(post("/api/pedidos")
                .contentType("application/json")
                .content(pedidoJson))
            .andExpect(status().isCreated())
            .andReturn();

        Long pedidoId = objectMapper.readTree(resultado.getResponse().getContentAsString())
            .get("id").asLong();

        // Simula o "restart": descarta tudo que a aplicação tinha em memória (cache do
        // Hibernate). Qualquer leitura seguinte precisa ir ao banco de fato.
        entityManager.clear();

        Pedido recuperado = pedidoRepository.findById(pedidoId).orElseThrow();
        assertNotNull(recuperado, "O pedido deveria continuar no banco após o cadastro");
        assertEquals(777, recuperado.getOrdemDeProducao());
        assertEquals(TipoPedido.SIMPLES, recuperado.getTipo());
        assertEquals(StatusPedido.PENDENTE, recuperado.getStatus());
        assertNotNull(recuperado.getRegistroCriacao(), "A data de criação deveria estar persistida");

        // O bloco associado (relacionamento JPA) também foi persistido junto.
        long blocosPersistidos = blocoRepository.findAll().stream()
            .filter(b -> b.getPedido().getId().equals(pedidoId))
            .count();
        assertEquals(1, blocosPersistidos);
    }

    @Test
    @DisplayName("Contagem de pedidos reflete o que foi gravado no banco")
    void deveContarPedidosGravadosNoBanco() throws Exception {
        String pedidoJson = """
            {
              "ordemDeProducao": 778,
              "tipo": "1",
              "corTampa": "1",
              "blocos": [ { "cor": "1", "andar": "1", "laminas": [] } ]
            }
            """;

        mockMvc.perform(post("/api/pedidos")
                .contentType("application/json")
                .content(pedidoJson))
            .andExpect(status().isCreated());

        entityManager.clear();
        assertEquals(1, pedidoRepository.count());
    }
}
