package com.smart.appsa.model.clp;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Estado da estação ESTOQUE (CLP 1) lido do DB9.
//
// <p>Além das flags comuns de {@link EstacaoInfoClp}, guarda a descrição completa
// do pedido por andar (cor do bloco, cores e padrões das três lâminas e tipo de
// processamento) e o estado do magazine de estoque.
@Component
@Getter
@Setter
@NoArgsConstructor
public class EstoqueInfoClp extends EstacaoInfoClp {
    // ----- Descrição do bloco do Andar 1 do pedido -----
    // Cor do bloco do andar 1 (0-VAZIO, 1-PRETO, 2-VERMELHO, 3-AZUL).
    private int cor_Andar_1;
    // Posição no magazine de estoque de onde o bloco do andar 1 deve ser retirado.
    private int posicao_Estoque_Andar_1;
    // Cor da 1ª lâmina do andar 1.
    private int cor_Lamina_1_Andar_1;
    // Cor da 2ª lâmina do andar 1.
    private int cor_Lamina_2_Andar_1;
    // Cor da 3ª lâmina do andar 1.
    private int cor_Lamina_3_Andar_1;
    // Padrão de usinagem da 1ª lâmina do andar 1.
    private int padrao_Lamina_1_Andar_1;
    // Padrão de usinagem da 2ª lâmina do andar 1.
    private int padrao_Lamina_2_Andar_1;
    // Padrão de usinagem da 3ª lâmina do andar 1.
    private int padrao_Lamina_3_Andar_1;
    // Tipo de processamento a aplicar no bloco do andar 1.
    private int processamento_Andar_1;

    // ----- Descrição do bloco do Andar 2 do pedido -----
    // Cor do bloco do andar 2 (0-VAZIO, 1-PRETO, 2-VERMELHO, 3-AZUL).
    private int cor_Andar_2;
    // Posição no magazine de estoque de onde o bloco do andar 2 deve ser retirado.
    private int posicao_Estoque_Andar_2;
    // Cor da 1ª lâmina do andar 2.
    private int cor_Lamina_1_Andar_2;
    // Cor da 2ª lâmina do andar 2.
    private int cor_Lamina_2_Andar_2;
    // Cor da 3ª lâmina do andar 2.
    private int cor_Lamina_3_Andar_2;
    // Padrão de usinagem da 1ª lâmina do andar 2.
    private int padrao_Lamina_1_Andar_2;
    // Padrão de usinagem da 2ª lâmina do andar 2.
    private int padrao_Lamina_2_Andar_2;
    // Padrão de usinagem da 3ª lâmina do andar 2.
    private int padrao_Lamina_3_Andar_2;
    // Tipo de processamento a aplicar no bloco do andar 2.
    private int processamento_Andar_2;

    // ----- Descrição do bloco do Andar 3 do pedido -----
    // Cor do bloco do andar 3 (0-VAZIO, 1-PRETO, 2-VERMELHO, 3-AZUL).
    private int cor_Andar_3;
    // Posição no magazine de estoque de onde o bloco do andar 3 deve ser retirado.
    private int posicao_Estoque_Andar_3;
    // Cor da 1ª lâmina do andar 3.
    private int cor_Lamina_1_Andar_3;
    // Cor da 2ª lâmina do andar 3.
    private int cor_Lamina_2_Andar_3;
    // Cor da 3ª lâmina do andar 3.
    private int cor_Lamina_3_Andar_3;
    // Padrão de usinagem da 1ª lâmina do andar 3.
    private int padrao_Lamina_1_Andar_3;
    // Padrão de usinagem da 2ª lâmina do andar 3.
    private int padrao_Lamina_2_Andar_3;
    // Padrão de usinagem da 3ª lâmina do andar 3.
    private int padrao_Lamina_3_Andar_3;
    // Tipo de processamento a aplicar no bloco do andar 3.
    private int processamento_Andar_3;

    // Número do pedido associado às informações de andares acima.
    private int numeroPedidoEst;
    // Quantidade de andares (blocos empilhados) do pedido.
    private int andares;
    // Posição reservada na expedição para o pedido.
    private int posicaoExpedicaoEst;

    // Pedido foi disparado para o CLP; baixada quando a estação confirma o início (ocupado).
    private boolean iniciarPedido;

    // Confirmação (Node -> CLP) de que a aplicação tratou a última adição/remoção no estoque.
    private boolean recebidoEstoque;
    // Solicita ao CLP que inicie a rotina de guardar o bloco na posição informada.
    private boolean iniciarGuardarEst;
    // Posição do magazine onde o CLP deve guardar o bloco (resposta a {@code pedirPosicaoEst}).
    private int posicaoGuardarEst;

    // Mapa de ocupação das 28 posições do magazine de estoque (1 byte por posição: cor armazenada).
    private byte[] posicoesOcupadas;

    // O CLP pediu uma posição livre para guardar um bloco recém-chegado.
    private boolean pedirPosicaoEst;
    // Posição do magazine que o CLP está adicionando/removendo no momento.
    private int posicaoEstoque;
    // O CLP indica que um bloco foi ADICIONADO em {@code posicaoEstoque}.
    private boolean adicionarEstoque;
    // O CLP indica que um bloco foi REMOVIDO de {@code posicaoEstoque}.
    private boolean removerEstoque;
    // O CLP informa que não há posição livre (magazine de estoque cheio).
    private boolean retornoEstoqueCheio;
    // Cor do bloco a guardar/adicionar na posição corrente.
    private int corGuardarEstoque;

    @Override
    protected byte getStatusByte() {
        return appStateConfig.getStatusEstoque();
    }
}
