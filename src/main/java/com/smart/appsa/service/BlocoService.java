package com.smart.appsa.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Lamina;
import com.smart.appsa.repository.BlocoRepository;

@Service
@RequiredArgsConstructor
public class BlocoService {

    private final BlocoRepository blocoRepository;
    private final EstoqueService estoqueService;
    private final LaminaService laminaService;

    // ─── READ ────────────────────────────────────────────────────────────────────

    public List<Bloco> listarTodos() {
        return blocoRepository.findAll();
    }

    public Bloco buscarPorId(Long id) {
        return blocoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bloco não encontrado com ID: " + id));
    }

    public List<Bloco> listarPorPedido(Long pedidoId) {
        return blocoRepository.findByPedidoId(pedidoId);
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────────

    public Bloco create(Bloco bloco) {

        // 2. Valida se a posição do estoque existe e não está vazia
        Estoque estoque = estoqueService.buscarPorId(bloco.getEstoque().getId());
        validarPosicaoEstoque(estoque, bloco);

        // 3. Valida máximo de lâminas se já vieram lâminas no objeto
        if (bloco.getLaminas() != null && !bloco.getLaminas().isEmpty()) {
            validarMaximoLaminas(bloco);
        }

        // 4. Salva o bloco
        Bloco blocoSalvo = blocoRepository.save(bloco);

        // 5. Marca posição do estoque como vazia após retirada do bloco
        estoqueService.removerBloco(estoque.getPosicaoFisica());

        return blocoSalvo;
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────────

    public Bloco atualizarBloco(Long id, Bloco dadosAtualizados) {
        Bloco blocoExistente = blocoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bloco não encontrado com ID: " + id));

        

        blocoExistente.setCor(dadosAtualizados.getCor());
        blocoExistente.setEstoque(dadosAtualizados.getEstoque());

        return blocoRepository.save(blocoExistente);
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────────

    public void removerBloco(Long id) {
        if (!blocoRepository.existsById(id)) {
            throw new RuntimeException("Bloco não encontrado com ID: " + id);
        }
        blocoRepository.deleteById(id);
    }

    // ─── VALIDAÇÕES ──────────────────────────────────────────────────────────────


    /**
     * Valida que o bloco não ultrapasse 3 lâminas.
     * Chamado pelo LaminaService antes de adicionar uma lâmina.
     */
    public void validarMaximoLaminas(Bloco bloco) {
        List<Lamina> laminas = bloco.getLaminas();
        if (laminas != null && laminas.size() >= 3) {
            throw new RuntimeException(
                    "Bloco ID " + bloco.getId() + " já possui o máximo de 3 lâminas.");
        }
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────────


  

    /**
     * Valida que a posição do estoque existe, não está vazia
     * e que a cor armazenada bate com a cor do bloco solicitado.
     */
    private void validarPosicaoEstoque(Estoque estoque, Bloco bloco) {
        if (estoque.getCorEstoque() == CorEstoque.VAZIO) {
            throw new RuntimeException(
                    "A posição " + estoque.getPosicaoFisica() + " do estoque está vazia.");
        }
        if (!estoque.getCorEstoque().name().equalsIgnoreCase(bloco.getCor().name())) {
            throw new IllegalArgumentException(
                    "A posição " + estoque.getPosicaoFisica() + " contém bloco de cor " +
                    estoque.getCorEstoque() + ", mas o pedido exige cor " + bloco.getCor() + ".");
        }
    }
}