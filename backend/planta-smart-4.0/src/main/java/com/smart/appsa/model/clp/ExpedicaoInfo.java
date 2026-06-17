package com.smart.appsa.model.clp;

public class ExpedicaoInfo extends EstacaoInfo {
    //********************** Expedição *************************
    //----------------------- NodeToPlc ------------------------
    /*---- StatusOP -------*/
    /*---- GerenciamentoExpedicao -------*/
    boolean recebidoExpedicao = false;
    boolean iniciarGuardarExp = false;
    int posicaoGuardarExp = 0;

    /*---- RemoverPedido -------*/
    int[] orderExpedicao = new int[12];

    //public static int posicaoExpedicaoSolicitada = 0;
    //----------------------- PlcToNode ------------------------
    

    /*---- GerenciamentoEstoque ------*/
    boolean pedirPosicaoExp = false;
    int posicaoGuardadoExpedicao = 0;
    int posicaoRemovidoExpedicao = 0;
    boolean adicionarExpedicao = false;
    boolean removerExpedicao = false;
    int opGuardadoExpedicao = 0;

}
