package com.smart.appsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smart.appsa.model.Lamina;

@Repository
public interface LaminaRepository extends JpaRepository<Lamina, Long> {

}
