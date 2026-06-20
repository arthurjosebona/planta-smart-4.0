package com.smart.appsa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smart.appsa.model.Expedicao;

public interface ExpedicaoRepository extends JpaRepository<Expedicao, Long> {

    boolean existsByOrdemDeProducaoAtual(int ordemDeProducaoAtual);
    Optional<Expedicao> findByPosicaoFisica(int posicaoFisica);
    Optional<Expedicao> findByOrdemDeProducaoAtual(int ordemDeProducaoAtual);
    Optional<Expedicao> findFirstByOrdemDeProducaoAtualOrderByPosicaoFisicaAsc(Integer ordemDeProducaoAtual);
    @Query("SELECT e.posicaoFisica FROM Expedicao e WHERE e.ordemDeProducaoAtual != 0")
    List<Integer> findPosicoesOcupadas();
}