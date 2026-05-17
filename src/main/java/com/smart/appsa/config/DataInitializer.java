package com.smart.appsa.config;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.repository.EstoqueRepository;
import com.smart.appsa.repository.ExpedicaoRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private ExpedicaoRepository expedicaoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (estoqueRepository.count() == 0) {
            System.out.println(">> Estoque está vazio. Inicializando dados.");
            estoqueRepository.saveAll(List.of(
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
            Estoque.builder().posicaoFisica(12).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(13).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(14).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(15).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(16).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(17).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(18).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(19).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(20).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(21).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(22).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(23).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(24).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(25).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(26).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(27).corEstoque(CorEstoque.VAZIO).build(),
            Estoque.builder().posicaoFisica(28).corEstoque(CorEstoque.VAZIO).build()
            ));
            System.out.println(">> Dados iniciais carregados com sucesso!");
        } else {
            System.out.println(">> Resetando estoque para estado inicial.");
            List<Estoque> estoques = estoqueRepository.findAll();
            
            Map<Integer, CorEstoque> coresIniciais = Map.of(
                1, CorEstoque.AZUL,
                2, CorEstoque.AZUL,
                3, CorEstoque.AZUL,
                4, CorEstoque.AZUL,
                5, CorEstoque.VERMELHO,
                6, CorEstoque.VERMELHO,
                7, CorEstoque.PRETO
            );

            estoques.forEach(e -> {
                CorEstoque cor = coresIniciais.getOrDefault(e.getPosicaoFisica(), CorEstoque.VAZIO);
                e.setCorEstoque(cor);
            });

            estoqueRepository.saveAll(estoques);
        }

        if (expedicaoRepository.count() == 0) {
            System.out.println(">> Expedicao está vazia. Inicializando dados.");
            expedicaoRepository.saveAll(List.of(
                Expedicao.builder().ordemDeProducaoAtual(1).posicaoFisica(1).build(),
                Expedicao.builder().ordemDeProducaoAtual(2).posicaoFisica(2).build(),
                Expedicao.builder().ordemDeProducaoAtual(3).posicaoFisica(3).build(),
                Expedicao.builder().ordemDeProducaoAtual(0).posicaoFisica(4).build(),
                Expedicao.builder().ordemDeProducaoAtual(0).posicaoFisica(5).build(),
                Expedicao.builder().ordemDeProducaoAtual(0).posicaoFisica(6).build(),
                Expedicao.builder().ordemDeProducaoAtual(0).posicaoFisica(7).build(),
                Expedicao.builder().ordemDeProducaoAtual(0).posicaoFisica(8).build(),
                Expedicao.builder().ordemDeProducaoAtual(0).posicaoFisica(9).build(),
                Expedicao.builder().ordemDeProducaoAtual(0).posicaoFisica(10).build(),
                Expedicao.builder().ordemDeProducaoAtual(0).posicaoFisica(11).build(),
                Expedicao.builder().ordemDeProducaoAtual(0).posicaoFisica(12).build()
            ));
            System.out.println(">> Dados iniciais de expedição carregados com sucesso!");
        } else {
            System.out.println(">> Resetando estoque para estado inicial.");
            List<Expedicao> expedicoes = expedicaoRepository.findAll();

            Map<Integer, Integer> ordensIniciais = Map.of(
                1, 1,
                2, 2,
                3, 3
            );

            expedicoes.forEach(e -> {
                e.setOrdemDeProducaoAtual(ordensIniciais.getOrDefault(e.getPosicaoFisica(), 0));
            });

            expedicaoRepository.saveAll(expedicoes);
        }
    }
}