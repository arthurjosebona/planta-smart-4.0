package com.smart.appsa.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smart.appsa.dto.clp.PedidoConfigDTO;
import com.smart.appsa.dto.clp.PedidoInfoDTO;
import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.exception.DuplicateAndarException;
import com.smart.appsa.exception.EstoqueInsuficienteException;
import com.smart.appsa.exception.InvalidOrdemDeProducaoException;
import com.smart.appsa.exception.OrdemDeProducaoExistenteException;
import com.smart.appsa.exception.PedidoNaoPendenteException;
import com.smart.appsa.exception.RequiredFieldException;
import com.smart.appsa.exception.TipoIncompativelComBlocosException;
import com.smart.appsa.exception.core.ResourceNotFoundException;
import com.smart.appsa.mapper.PedidoMapper;
import com.smart.appsa.model.Bloco;
import com.smart.appsa.model.Estoque;
import com.smart.appsa.model.Expedicao;
import com.smart.appsa.model.Pedido;
import com.smart.appsa.model.enums.AndarBloco;
import com.smart.appsa.model.enums.CorEstoque;
import com.smart.appsa.model.enums.StatusPedido;
import com.smart.appsa.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final BlocoService blocoService;
    private final EstoqueService estoqueService;
    private final ExpedicaoService expedicaoService;
    private final SmartService smartService;

    @Transactional
    public PedidoResponseDTO create(PedidoRequestDTO requestDTO) {
        validatePedido(requestDTO);
        Pedido pedido = PedidoMapper.mapEntityByRequestDTO(requestDTO);
        pedido.setRegistroCriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.PENDENTE);
        Pedido saved = pedidoRepository.save(pedido);
        createBlocks(saved, requestDTO.blocos());
        return PedidoMapper.mapDto(pedidoRepository.findById(saved.getId()).get());
    }

    private void validatePedido(PedidoRequestDTO requestDTO) {
        validateRequiredFields(requestDTO);
        validateBusinessRules(requestDTO);
    }

    private void validateRequiredFields(PedidoRequestDTO requestDTO) {
        if (requestDTO == null)
            throw new RequiredFieldException("PedidoRequestDTO");
        if (requestDTO.blocos() == null || requestDTO.blocos().isEmpty())
            throw new RequiredFieldException("Blocos");
        if (requestDTO.tipo() == null)
            throw new RequiredFieldException("Tipo Pedido");
        if (requestDTO.corTampa() == null)
            throw new RequiredFieldException("Cor da tampa");
    }

    private void validateBusinessRules(PedidoRequestDTO requestDTO) {
        if (requestDTO.ordemDeProducao() <= 0)
            throw new InvalidOrdemDeProducaoException(requestDTO.ordemDeProducao());
        if (requestDTO.tipo().getValue() != requestDTO.blocos().size())
            throw new TipoIncompativelComBlocosException(requestDTO.tipo(), requestDTO.blocos().size());
        if (pedidoRepository.existsByOrdemDeProducao(requestDTO.ordemDeProducao()))
            throw new OrdemDeProducaoExistenteException(requestDTO.ordemDeProducao());
        validateDuplicateAndar(requestDTO.blocos());
    }

    private void validateDuplicateAndar(List<Bloco> blocos) {
        Map<AndarBloco, Long> countByAndar = blocos.stream()
                .collect(Collectors.groupingBy(
                        Bloco::getAndar,
                        Collectors.counting()));

        List<AndarBloco> duplicateAndar = countByAndar.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList();

        if (!duplicateAndar.isEmpty())
            throw new DuplicateAndarException(duplicateAndar);
    }

    private void createBlocks(Pedido pedido, List<Bloco> blocos) {
        // Pedidos podem ser criados independentemente da quantidade em estoque.
        // O slot físico e a baixa do estoque só acontecem no envio para produção
        for (Bloco bloco : blocos) {
            bloco.setPedido(pedido);
            bloco.setEstoque(null);
            blocoService.create(bloco);
        }
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO findById(Long id) {
        return PedidoMapper.mapDto(pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id)));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAll() {
        return pedidoRepository.findAll()
                .stream()
                .map(p -> PedidoMapper.mapDto(p))
                .toList();
    }

    @Transactional
    public PedidoResponseDTO startProduction(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
        assignEstoqueSlots(pedido);
        prepareForCompletion(pedido);
        Pedido updated = saveWithExpedition(pedido);
        System.out.println("DTOs PARA A BANCADA: ");
        PedidoInfoDTO infoDTO = PedidoMapper.mapToInfoDTOByEntity(updated);
        PedidoConfigDTO configDTO = PedidoMapper.mapToConfigDTOByEntity(updated);

        System.out.println(infoDTO.toString());
        System.out.println(configDTO.toString());

        smartService.enviarParaProducao(configDTO, infoDTO);

        return PedidoMapper.mapDto(updated);
    }

    private void prepareForCompletion(Pedido pedido) {
        pedido.setStatus(StatusPedido.PRODUCAO);
        // pedido.setRegistroEntradaExpedicao(LocalDateTime.now());
    }

    private Pedido saveWithExpedition(Pedido pedido) {
        Expedicao nextFree = expedicaoService.findFirstPosicaoLivre();
        pedido.setExpedicao(nextFree);
        // Antes a OP já era escrita no banco nessa etapa, agora fica quando receber via EstoqueComm
        // expedicaoService.assignOrdemAtPosicao(pedido.getOrdemDeProducao(), nextFree.getPosicaoFisica());
        return pedidoRepository.save(pedido);
    }

    //  Verifica a disponibilidade física no estoque e vincula cada bloco a uma posição
    //  realmente ocupada da sua cor. Só aqui a quantidade é validada, a criação do pedido
    //  é independente do estoque. 
    private void assignEstoqueSlots(Pedido pedido) {
        Map<CorEstoque, List<Bloco>> blocosPorCor = pedido.getBlocos().stream()
                .collect(Collectors.groupingBy(b -> CorEstoque.fromValue(b.getCor().getValue())));

        for (Map.Entry<CorEstoque, List<Bloco>> entry : blocosPorCor.entrySet()) {
            CorEstoque cor = entry.getKey();
            List<Bloco> blocos = entry.getValue();

            List<Estoque> disponiveis = estoqueService.findByCorEstoque(cor);
            if (disponiveis.size() < blocos.size())
                throw new EstoqueInsuficienteException(cor.name());

            for (int i = 0; i < blocos.size(); i++) {
                blocos.get(i).setEstoque(disponiveis.get(i));
            }
        }
    }

    @Transactional
    public void delete(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        if (pedido.getStatus() != StatusPedido.PENDENTE)
            throw new PedidoNaoPendenteException("excluir", pedido.getStatus());

        // O estado físico do estoque é de responsabilidade do CLP (EstoqueCommService),
        // por isso o delete não restaura cor de estoque — evita estoque fantasma.
        pedidoRepository.delete(pedido);
    }

    @Transactional
    public PedidoResponseDTO update(Long id, PedidoRequestDTO requestDTO) {
        Pedido existing = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));

        if (existing.getStatus() != StatusPedido.PENDENTE)
            throw new PedidoNaoPendenteException("atualizar", existing.getStatus());

        validatePedidoForUpdate(requestDTO, id);

        // Remove os blocos antigos (estoque físico é gerido pelo CLP, não restaurado aqui)
        blocoService.deleteAllByPedido(existing);

        // Atualiza os campos do pedido
        existing.setOrdemDeProducao(requestDTO.ordemDeProducao());
        existing.setTipo(requestDTO.tipo());
        existing.setCorTampa(requestDTO.corTampa());
        Pedido saved = pedidoRepository.save(existing);

        // Recria os blocos com os novos dados
        createBlocks(saved, requestDTO.blocos());

        return PedidoMapper.mapDto(pedidoRepository.findById(saved.getId()).get());
    }

    private void validatePedidoForUpdate(PedidoRequestDTO requestDTO, Long currentId) {
        validateRequiredFields(requestDTO);

        if (requestDTO.ordemDeProducao() <= 0)
            throw new InvalidOrdemDeProducaoException(requestDTO.ordemDeProducao());

        if (requestDTO.tipo().getValue() != requestDTO.blocos().size())
            throw new TipoIncompativelComBlocosException(requestDTO.tipo(), requestDTO.blocos().size());

        // Verifica duplicata de ordemDeProducao ignorando o próprio pedido
        pedidoRepository.findByOrdemDeProducao(requestDTO.ordemDeProducao())
                .filter(p -> !p.getId().equals(currentId))
                .ifPresent(p -> {
                    throw new OrdemDeProducaoExistenteException(requestDTO.ordemDeProducao());
                });

        validateDuplicateAndar(requestDTO.blocos());
    }
    
    @Transactional(readOnly = true)
    public PedidoResponseDTO findByOp(Integer op) {
        return PedidoMapper.mapDto(
            pedidoRepository.findByOrdemDeProducao(op)
                .orElseThrow(() -> new ResourceNotFoundException("pedido", "ordem de produção", op))
        );
    }
}
