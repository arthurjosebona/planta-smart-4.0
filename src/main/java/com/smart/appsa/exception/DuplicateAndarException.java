package com.smart.appsa.exception;

import java.util.List;

import com.smart.appsa.model.enums.AndarBloco;

public class DuplicateAndarException extends BusinessException{

    public DuplicateAndarException(List<AndarBloco> andaresDuplicados) {
        super(String.format(
            "Não é permitido blocos com andares duplicados: %s", 
            andaresDuplicados.stream().map(AndarBloco::name).toList()
        ));
    }

}
