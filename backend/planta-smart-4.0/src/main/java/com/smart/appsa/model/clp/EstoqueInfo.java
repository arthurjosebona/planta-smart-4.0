package com.smart.appsa.model.clp;

public class EstoqueInfo {
    
    //********************** Estoque ***************************
    //----------------------- NodeToPlc ------------------------
    /*---- InformacaoPedido -------*/
    // InfoPedido
    int cor_Andar_1 = 0;
    int posicao_Estoque_Andar_1 = 0;
    int cor_Lamina_1_Andar_1 = 0;
    int cor_Lamina_2_Andar_1 = 0;
    int cor_Lamina_3_Andar_1 = 0;
    int padrao_Lamina_1_Andar_1 = 0;
    int padrao_Lamina_2_Andar_1 = 0;
    int padrao_Lamina_3_Andar_1 = 0;
    int processamento_Andar_1 = 0;

    int cor_Andar_2 = 0;
    int posicao_Estoque_Andar_2 = 0;
    int cor_Lamina_1_Andar_2 = 0;
    int cor_Lamina_2_Andar_2 = 0;
    int cor_Lamina_3_Andar_2 = 0;
    int padrao_Lamina_1_Andar_2 = 0;
    int padrao_Lamina_2_Andar_2 = 0;
    int padrao_Lamina_3_Andar_2 = 0;
    int processamento_Andar_2 = 0;

    int cor_Andar_3 = 0;
    int posicao_Estoque_Andar_3 = 0;
    int cor_Lamina_1_Andar_3 = 0;
    int cor_Lamina_2_Andar_3 = 0;
    int cor_Lamina_3_Andar_3 = 0;
    int padrao_Lamina_1_Andar_3 = 0;
    int padrao_Lamina_2_Andar_3 = 0;
    int padrao_Lamina_3_Andar_3 = 0;
    int processamento_Andar_3 = 0;

    int numeroPedidoEst = 0;
    int andares = 0;
    int posicaoExpedicaoEst = 0;

    boolean iniciarPedido = false;

    /*---- GerenciamentoEstoque -------*/
    boolean recebidoEstoque = false;
    boolean iniciarGuardarEst = false;
    int posicaoGuardarEst = 0;

    /*---- PosicoesOcupadas -------*/
    byte[] posicoesOcupadas = new byte[28];

    //----------------------- PlcToNode
    /*---- GerenciamentoEstoque ------*/
    boolean pedirPosicaoEst = false;
    int posicaoEstoque = 0;
    boolean adicionarEstoque = false;
    boolean removerEstoque = false;
    boolean retornoEstoqueCheio = false;
    int corGuardarEstoque = 0;

    /*---- RealidadeAumentada */
    boolean xEmergenciaAtivadaEst = false;
    boolean xComutadorAutomaticoEst = false;
    boolean xNecessitaHomeEixoVerticalEst = false;
    boolean xNecessitaHomeEixoGiroEst = false;
    boolean xServoDesligadoEixoVerticalEst = false;
    boolean xServoDesligadoEixoGiroEst = false;
    boolean xCondicaoIniciarEst = false;
}
