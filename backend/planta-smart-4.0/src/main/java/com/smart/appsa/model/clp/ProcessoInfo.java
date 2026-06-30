package com.smart.appsa.model.clp;

import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;

// Estado da estação PROCESSO (CLP 2) lido do DB2.
//
// <p>O processo só expõe as flags comuns de operação e estado herdadas de
// {@link EstacaoInfoClp} (recebidoOp, start/finish/cancelOP, ocupado, etc.);
// por isso não acrescenta atributos próprios.
@Component
@NoArgsConstructor
public class ProcessoInfo extends EstacaoInfoClp {

    @Override
    protected byte getStatusByte() {
        return appStateConfig.getStatusProcesso();
    }
}
