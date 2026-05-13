package com.smart.appsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smart.appsa.model.Expedicao;

@Repository
public interface ExpedicaoRepository extends JpaRepository<Expedicao, Long> {

}
