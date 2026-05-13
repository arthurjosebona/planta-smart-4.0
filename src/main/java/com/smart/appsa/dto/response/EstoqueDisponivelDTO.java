package com.smart.appsa.dto.response;

import java.util.List;

import com.smart.appsa.model.enums.CorEstoque;

public record EstoqueDisponivelDTO(
    List<CorEstoque> posicoesNaoVazias
) {} 
