package com.smart.appsa.model.clp;

import org.springframework.stereotype.Component;

import com.smart.appsa.config.AppStateConfig;

import lombok.Getter;
import lombok.Setter;

// Estado da estação MONTAGEM (CLP 3) lido do DB57.
//
// <p>A montagem só expõe as flags comuns de operação e estado herdadas de
// {@link EstacaoInfoClp} (recebidoOp, start/finish/cancelOP, ocupado, etc.);
// por isso não acrescenta atributos próprios.
@Component
@Getter
@Setter
public class MontagemInfoClp extends EstacaoInfoClp {

    private String supervisorioEstoque;
    private String supervisorioProcesso;
    private String supervisorioMontagem;
    private String supervisorioExpedicao;

    public MontagemInfoClp(AppStateConfig appStateConfig) {
        super(appStateConfig);
    }

    @Override
    protected byte getStatusByte() {
        return appStateConfig.getStatusMontagem();
    }
}
