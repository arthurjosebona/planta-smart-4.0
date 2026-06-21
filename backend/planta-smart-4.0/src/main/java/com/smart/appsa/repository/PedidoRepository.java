package com.smart.appsa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.appsa.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByOrdemDeProducao(Integer ordemDeProducao);
    Optional<Pedido> findByOrdemDeProducao(int ordemDeProducao);
    List<Pedido> findByExpedicaoId(Long idExpedicao);
}
