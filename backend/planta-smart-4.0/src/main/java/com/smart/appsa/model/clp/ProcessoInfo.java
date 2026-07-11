package com.smart.appsa.model.clp;

import org.springframework.stereotype.Component;

import com.smart.appsa.config.AppStateConfig;

// Estado da estação PROCESSO (CLP 2) lido do DB2.
//
// <p>O processo só expõe as flags comuns de operação e estado herdadas de
// {@link EstacaoInfoClp} (recebidoOp, start/finish/cancelOP, ocupado, etc.);
// por isso não acrescenta atributos próprios.
@Component
public class ProcessoInfo extends EstacaoInfoClp {

    public ProcessoInfo(AppStateConfig appStateConfig) {
        super(appStateConfig);
    }

    @Override
    protected byte getStatusByte() {
        return appStateConfig.getStatusProcesso();
    }
}
