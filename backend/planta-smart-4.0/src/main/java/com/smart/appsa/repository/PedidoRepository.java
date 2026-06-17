package com.smart.appsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.smart.appsa.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    boolean existsByOrdemDeProducao(Integer ordemDeProducao);
    Optional<Pedido> findByOrdemDeProducao(int ordemDeProducao);
}
