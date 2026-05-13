package com.smart.appsa.repository;

import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.enums.CorEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    List<Estoque> findByCorEstoqueNot(CorEstoque corEstoque);
   
    Optional<Estoque> findByPosicaoFisica(int posicaoFisica);
}