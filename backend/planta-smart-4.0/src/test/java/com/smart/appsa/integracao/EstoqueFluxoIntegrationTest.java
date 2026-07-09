package com.smart.appsa.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.repository.EstoqueRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EstoqueFluxoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private AppStateConfig appStateConfig;

    private Long idPosicaoVazia;

    @BeforeEach
    void preparar() {
        estoqueRepository.deleteAll();
        // Impede qualquer escrita real no CLP durante o teste.
        appStateConfig.setReadOnly(true);

        Estoque vazia = estoqueRepository.save(Estoque.builder()
            .posicaoFisica(1)
            .corEstoque(CorEstoque.VAZIO)
            .build());
        idPosicaoVazia = vazia.getId();
    }

    @Test
    @DisplayName("PUT /api/estoque grava a nova cor no banco e a posição passa a ficar disponível")
    void deveAtualizarCorDoEstoqueEPersistirNoBanco() throws Exception {
        String body = """
            [ { "id": %d, "posicaoFisica": 1, "corEstoque": "3" } ]
            """.formatted(idPosicaoVazia);

        mockMvc.perform(put("/api/estoque")
                .contentType("application/json")
                .content(body))
            .andExpect(status().isNoContent());

        // A nova cor foi persistida.
        Estoque atualizada = estoqueRepository.findById(idPosicaoVazia).orElseThrow();
        assertEquals(CorEstoque.AZUL, atualizada.getCorEstoque());

        // E agora a posição aparece como disponível (não mais VAZIO).
        mockMvc.perform(get("/api/estoque/disponivel"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == " + idPosicaoVazia + ")]").exists());
    }
}
