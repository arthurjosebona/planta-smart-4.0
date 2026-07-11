package com.smart.appsa.model.clp;

import org.springframework.stereotype.Component;

import com.smart.appsa.config.AppStateConfig;

import lombok.Getter;
import lombok.Setter;

// Estado da estação EXPEDIÇÃO (CLP 4) lido do DB9.
//
// <p>Além das flags comuns de {@link EstacaoInfoClp}, guarda o conteúdo do
// magazine de expedição (qual OP está em cada uma das 12 posições) e as flags de
// adição/remoção de pedidos finalizados.
@Getter
@Setter
@Component
public class ExpedicaoInfoClp extends EstacaoInfoClp {

    public ExpedicaoInfoClp(AppStateConfig appStateConfig) {
        super(appStateConfig);
    }

    // Confirmação (Node -> CLP) de que a aplicação tratou a última adição/remoção na expedição.
    boolean recebidoExpedicao;
    // Solicita ao CLP que inicie a rotina de guardar o bloco na posição informada.
    boolean iniciarGuardarExp;
    // Posição do magazine onde o CLP deve guardar o bloco (resposta a {@code pedirPosicaoExp}).
    int posicaoGuardarExp;

    // Conteúdo das 12 posições do magazine de expedição (OP armazenada em cada posição; 0 = livre).
    int[] orderExpedicao;

    // O CLP pediu uma posição livre para guardar um pedido finalizado.
    boolean pedirPosicaoExp;
    // Posição em que o CLP confirmou ter guardado o último bloco.
    int posicaoGuardadoExpedicao;
    // Posição de onde o CLP removeu o último bloco (pedido expedido/retirado).
    int posicaoRemovidoExpedicao;
    // O CLP indica que um pedido foi ADICIONADO ao magazine de expedição.
    boolean adicionarExpedicao;
    // O CLP indica que um pedido foi REMOVIDO do magazine de expedição.
    boolean removerExpedicao;
    // Número da OP que está sendo guardada/removida no momento.
    int opGuardadoExpedicao;

    @Override
    protected byte getStatusByte() {
        return appStateConfig.getStatusExpedicao();
    }
}
