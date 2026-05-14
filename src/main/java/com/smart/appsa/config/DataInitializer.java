package com.smart.appsa.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.repository.EstoqueRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EstoqueRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            System.out.println(">> Estoque já contém dados. Pulando inicialização.");
            return;
        }
        repository.saveAll(List.of(
            Estoque.builder().posicaoFisica(1).corEstoque(CorEstoque.AZUL).build(),
            Estoque.builder().posicaoFisica(2).corEstoque(CorEstoque.AZUL).build(),
            Estoque.builder().posicaoFisica(3).corEstoque(CorEstoque.AZUL).build(),
            Estoque.builder().posicaoFisica(4).corEstoque(CorEstoque.AZUL).build(),
            Estoque.builder().posicaoFisica(5).corEstoque(CorEstoque.VERMELHO).build(),
            Estoque.builder().posicaoFisica(6).corEstoque(CorEstoque.VERMELHO).build(),
            Estoque.builder().posicaoFisica(7).corEstoque(CorEstoque.PRETO).build(),
            Estoque.builder().posicaoFisica(8).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(9).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(10).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(11).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(12).corEstoque(CorEstoque.VAZIO).build()
        ));

        System.out.println(">> Dados iniciais carregados com sucesso!");
    }
}