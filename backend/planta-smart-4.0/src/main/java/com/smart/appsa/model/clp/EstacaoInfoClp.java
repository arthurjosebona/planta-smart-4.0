package com.smart.appsa.model.clp;

import org.springframework.beans.factory.annotation.Autowired;

import com.smart.appsa.config.AppStateConfig;
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

    @Autowired
    protected AppStateConfig appStateConfig;

    // Retorna o byte de status global da bancada correspondente a esta estação.
    protected abstract byte getStatusByte();

    // Deriva o apresentado no front a partir do status global da bancada.
    public StatusEstacao getStatus() {
        return switch (getStatusByte()) {
            case 1 -> StatusEstacao.OCUPADO;
            case 2 -> StatusEstacao.AGUARDANDO;
            default -> StatusEstacao.PARADO;
        };
    }
}
