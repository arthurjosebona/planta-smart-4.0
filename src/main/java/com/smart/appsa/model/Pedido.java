package com.smart.appsa.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_sa_pedido")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int ordemDeProducao;
    @OneToMany(mappedBy = "t_sa_bloco")
    @JsonManagedReference
    private List<Bloco> blocos;
    @Enumerated(EnumType.ORDINAL)
    private StatusPedido status;
    @Enumerated(EnumType.ORDINAL)
    private TipoPedido tipo;
    @Enumerated(EnumType.ORDINAL)
    private CorTampa corTampa;
    private LocalDateTime registroCriacao;
    private LocalDateTime registroEntradaExpedicao;
    private LocalDateTime registroSaidaExpedicao;
    @ManyToOne
    @JoinColumn(name = "id_expedicao")
    @JsonBackReference
    private Expedicao expedicao;
}
