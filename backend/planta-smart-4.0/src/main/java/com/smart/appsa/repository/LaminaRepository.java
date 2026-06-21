package com.smart.appsa.repository;

import com.smart.appsa.model.Lamina;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import com.smart.appsa.model.Bloco;

public interface LaminaRepository extends JpaRepository<Lamina, Long> {
    List<Lamina> findByBlocoId(Long blocoId);
    List<Lamina> findByBloco(Bloco bloco);
}