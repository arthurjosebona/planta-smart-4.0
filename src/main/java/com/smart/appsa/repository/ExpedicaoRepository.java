package com.smart.appsa.repository;

import com.smart.appsa.model.Expedicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpedicaoRepository extends JpaRepository<Expedicao, Long> {

    boolean existsByOrdemDeProducaoAtual(int ordemDeProducaoAtual);

    Optional<Expedicao> findByOrdemDeProducaoAtual(int ordemDeProducaoAtual);

    @Query("SELECT e.posicaoFisica FROM Expedicao e")
    List<Integer> findPosicoesOcupadas();
}