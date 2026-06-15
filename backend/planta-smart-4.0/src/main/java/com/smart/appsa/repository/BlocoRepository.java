
package com.smart.appsa.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Pedido;


@Repository
public interface BlocoRepository extends JpaRepository<Bloco, Long> {
    @Transactional
   
    void deleteAllByPedido(Pedido pedido);
}
