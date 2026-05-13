package com.smart.appsa.model;

import com.smart.appsa.model.enums.CorLamina;
import com.smart.appsa.model.enums.PadraoLamina;
import com.smart.appsa.model.enums.PosicaoLamina;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Long id;
    @Enumerated(EnumType.ORDINAL)
    private CorLamina cor;
    @Enumerated(EnumType.ORDINAL)
    private PadraoLamina padrao;
    @Enumerated(EnumType.ORDINAL)
    private PosicaoLamina posicao;
    @ManyToOne
    @JoinColumn(name = "id_bloco")
    private Bloco bloco;
}
