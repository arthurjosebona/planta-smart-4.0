package com.smart.appsa.model.clp;

import org.springframework.stereotype.Component;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
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
