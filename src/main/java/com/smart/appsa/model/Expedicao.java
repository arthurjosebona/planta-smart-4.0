package com.smart.appsa.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_sa_expedicao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expedicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int posicaoFisica;
    private int ordemDeProducaoAtual;
    @OneToMany(mappedBy = "t_sa_pedido")
    @JsonManagedReference
    private List<Pedido> pedidos;
}
