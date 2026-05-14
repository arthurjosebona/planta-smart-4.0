package com.smart.appsa.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.smart.appsa.model.enums.CorTampa;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.model.enums.TipoPedido;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
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
    @Column(name = "id_pedido")
    private Long id;
    @Column(name = "vl_ordem_de_producao", nullable = false)
    private Integer ordemDeProducao;
    @OneToMany(mappedBy = "pedido")
    @JsonManagedReference
    private List<Bloco> blocos;
    @Column(name = "vl_status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private StatusPedido status;
    @Column(name = "tp_pedido", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TipoPedido tipo;
    @Column(name = "vl_cor_tampa", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CorTampa corTampa;
    @Column(name = "rg_criacao", nullable = false)
    private LocalDateTime registroCriacao;
    @Column(name = "rg_entrada_expedicao")
    private LocalDateTime registroEntradaExpedicao;
    @Column(name = "rg_saida_expedicao")
    private LocalDateTime registroSaidaExpedicao;
    @ManyToOne
    @JoinColumn(
        name = "id_expedicao",
        foreignKey = @ForeignKey(name = "fk_expedicao_pedido")
    )
    @JsonBackReference
    private Expedicao expedicao;
}
