package com.smart.appsa.repository;

import com.smart.appsa.model.Lamina;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.smart.appsa.model.Bloco;

@Repository
public interface LaminaRepository extends JpaRepository<Lamina, Long> {
    List<Lamina> findByBlocoId(Long blocoId);
    List<Lamina> findByBloco(Bloco bloco);
}