package com.smart.appsa.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.smart.appsa.model.enums.CorBloco;

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
@Table(name = "t_sa_bloco")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Bloco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bloco")
    private Long id;
    @ManyToOne
    @JoinColumn(
        name = "id_pedido",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_pedido_bloco")
    )
    @JsonBackReference
    private Pedido pedido;
    @ManyToOne
    @JoinColumn(
        name = "id_estoque",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_estoque_bloco")
    )
    private Estoque estoque;
    @OneToMany(mappedBy = "bloco")
    @JsonManagedReference
    private List<Lamina> laminas;
    @Column(name = "vl_cor", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private CorBloco cor;

}
