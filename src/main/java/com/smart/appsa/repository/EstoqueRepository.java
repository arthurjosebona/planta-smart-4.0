package com.smart.appsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smart.appsa.model.Estoque;

@Repository
public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

}
