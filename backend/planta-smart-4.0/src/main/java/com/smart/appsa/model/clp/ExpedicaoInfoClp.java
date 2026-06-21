package com.smart.appsa.model.clp;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Component
public class ExpedicaoInfoClp extends EstacaoInfoClp {
    boolean recebidoExpedicao;
    boolean iniciarGuardarExp;
    int posicaoGuardarExp;

    int[] orderExpedicao;

    boolean pedirPosicaoExp;
    int posicaoGuardadoExpedicao;
    int posicaoRemovidoExpedicao;
    boolean adicionarExpedicao;
    boolean removerExpedicao;
    int opGuardadoExpedicao;
}
