package com.smart.appsa.model.clp;

import com.smart.appsa.model.enums.StatusEstacao;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class EstacaoInfoClp {
    // Confirmação (Node -> CLP) de que a aplicação recebeu a ordem de produção atual.
    private boolean recebidoOp;
    // Número da ordem de produção (OP) que a estação está executando no momento.
    private int numeroOP;

    // A estação sinalizou que FINALIZOU a operação da OP corrente.
    private boolean finishOP;
    // A estação sinalizou que INICIOU a operação da OP corrente.
    private boolean startOP;
    // A estação sinalizou que a OP corrente foi CANCELADA.
    private boolean cancelOP;

    // A estação está em modo manual (operada localmente, fora do controle do supervisório).
    private boolean manual;
    // A estação está em estado de emergência (botão de emergência acionado).
    private boolean emergencia;
    // A estação está ocupada executando uma operação.
    private boolean ocupado;
    // A estação está livre, aguardando a próxima operação.
    private boolean aguardando;

    // Deriva o {@link StatusEstacao} apresentado no front a partir das flags de estado.
    public StatusEstacao getStatus() {
        if (ocupado) return StatusEstacao.OCUPADO;
        if (manual) return StatusEstacao.MANUAL;
        if (emergencia) return StatusEstacao.EMERGENCIA;
        return StatusEstacao.AGUARDANDO;
    }
}
