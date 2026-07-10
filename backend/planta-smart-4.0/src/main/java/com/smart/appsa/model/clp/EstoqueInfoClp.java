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
    private int corAndar1;
    private int posicaoEstoqueAndar1;
    private int corLamina1Andar1;
    private int corLamina2Andar1;
    private int corLamina3Andar1;
    private int padraoLamina1Andar1;
    private int padraoLamina2Andar1;
    private int padraoLamina3Andar1;
    private int processamentoAndar1;
    private int corAndar2;
    private int posicaoEstoqueAndar2;
    private int corLamina1Andar2;
    private int corLamina2Andar2;
    private int corLamina3Andar2;
    private int padraoLamina1Andar2;
    private int padraoLamina2Andar2;
    private int padraoLamina3Andar2;
    private int processamentoAndar2;
    private int corAndar3;
    private int posicaoEstoqueAndar3;
    private int corLamina1Andar3;
    private int corLamina2Andar3;
    private int corLamina3Andar3;
    private int padraoLamina1Andar3;
    private int padraoLamina2Andar3;
    private int padraoLamina3Andar3;
    private int processamentoAndar3;
    private int numeroPedido;
    private int posicaoExpedicao;

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
