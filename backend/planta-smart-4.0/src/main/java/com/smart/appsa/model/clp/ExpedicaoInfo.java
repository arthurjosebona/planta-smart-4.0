package com.smart.appsa.model.clp;

public class ExpedicaoInfo extends EstacaoInfo {
    boolean recebidoExpedicao = false;
    boolean iniciarGuardarExp = false;
    int posicaoGuardarExp = 0;

    int[] orderExpedicao = new int[12];

    boolean pedirPosicaoExp = false;
    int posicaoGuardadoExpedicao = 0;
    int posicaoRemovidoExpedicao = 0;
    boolean adicionarExpedicao = false;
    boolean removerExpedicao = false;
    int opGuardadoExpedicao = 0;
}
