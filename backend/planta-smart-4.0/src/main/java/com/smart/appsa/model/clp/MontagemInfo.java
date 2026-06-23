package com.smart.appsa.model.clp;

import org.springframework.stereotype.Component;

import lombok.NoArgsConstructor;

// Estado da estação MONTAGEM (CLP 3) lido do DB57.
//
// <p>A montagem só expõe as flags comuns de operação e estado herdadas de
// {@link EstacaoInfoClp} (recebidoOp, start/finish/cancelOP, ocupado, etc.);
// por isso não acrescenta atributos próprios.
@Component
@NoArgsConstructor
public class MontagemInfo extends EstacaoInfoClp {

}
