package com.smart.appsa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.smart.appsa.model.enums.CorLamina;
import com.smart.appsa.model.enums.PadraoLamina;
import com.smart.appsa.model.enums.PosicaoLamina;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_sa_lamina")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lamina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lamina")
    private Long id;
    @Column(name = "vl_cor", nullable = false)
    private CorLamina cor;
    @Column(name = "vl_padrao", nullable = false)
    private PadraoLamina padrao;
    @Column(name = "vl_posicao", nullable = false)
    private PosicaoLamina posicao;
    @ManyToOne
    @JoinColumn(
        name = "id_bloco",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_bloco_lamina")
    )
    @JsonBackReference
    private Bloco bloco;
}
