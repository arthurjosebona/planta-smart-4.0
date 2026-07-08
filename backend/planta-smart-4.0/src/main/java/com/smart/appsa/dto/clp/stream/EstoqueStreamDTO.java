package com.smart.appsa.dto.clp.stream;

import java.time.LocalDateTime;
import java.util.List;

import com.smart.appsa.model.enums.StatusEstacao;

// DTO de stream da estação ESTOQUE. Inclui, além dos campos da estação, os bytes de
// status de todas as bancadas (equivalente ao antigo {@code getEstoqueComStatus}).
public record EstoqueStreamDTO(
        String estacao,
        StatusEstacao status,
        int numeroOP,
        boolean ocupado,
        boolean aguardando,
        boolean manual,
        boolean emergencia,
        boolean iniciarPedido,
        boolean pedirPosicaoEst,
        boolean adicionarEstoque,
        boolean removerEstoque,
        boolean retornoEstoqueCheio,
        boolean recebidoEstoque,
        boolean iniciarGuardarEst,
        int posicaoEstoque,
        int posicaoGuardarEst,
        int corGuardarEstoque,
        List<Integer> posicoesOcupadas,
        int statusEstoque,
        int statusProcesso,
        int statusMontagem,
        int statusExpedicao,
        int statusProducao,
        boolean pedidoEmCurso,
        LocalDateTime registroInicioPedido
) {}
