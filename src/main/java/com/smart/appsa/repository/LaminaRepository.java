package com.smart.appsa.repository;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaminaRepository extends JpaRepository<Lamina, Long> {

    List<Lamina> findByBloco(Bloco bloco);
}