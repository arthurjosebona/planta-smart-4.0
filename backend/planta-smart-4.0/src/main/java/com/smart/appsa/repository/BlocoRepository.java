package com.smart.appsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Pedido;

public interface BlocoRepository extends JpaRepository<Bloco, Long> {
    void deleteAllByPedido(Pedido pedido);
}
