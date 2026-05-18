package com.smart.appsa.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    @Column(name = "id_expedicao")
    private Long id;
    @Column(name = "vl_posicao_fisica", nullable = false, unique = true)
    @Min(1)
    @Max(12)
    private Integer posicaoFisica;
    @Column(name = "vl_op_atual")
    private Integer ordemDeProducaoAtual;
    @OneToMany(mappedBy = "expedicao")
    @JsonManagedReference
    private List<Pedido> pedidos;
}
