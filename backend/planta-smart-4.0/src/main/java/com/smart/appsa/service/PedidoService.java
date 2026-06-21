package com.smart.appsa.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.smart.appsa.dto.request.PedidoRequestDTO;
import com.smart.appsa.dto.response.PedidoResponseDTO;
import com.smart.appsa.exception.DuplicateAndarException;
import com.smart.appsa.exception.EstoqueInsuficienteException;
import com.smart.appsa.exception.InvalidOrdemDeProducaoException;
import com.smart.appsa.exception.OrdemDeProducaoExistenteException;
import com.smart.appsa.exception.PedidoNaoPendenteException;
import com.smart.appsa.exception.RequiredFieldException;
import com.smart.appsa.exception.TipoIncompativelComBlocosException;
import com.smart.appsa.exception.core.BusinessException;
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
            throw new BusinessException("Pedido já existe com ordem de produção " + requestDTO.ordemDeProducao());
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
      
        validatePedidoForUpdate(requestDTO, id);

        // Remove os blocos antigos (estoque físico é gerido pelo CLP, não restaurado aqui)
        blocoService.deleteAllByPedido(existing);

        existing.setOrdemDeProducao(requestDTO.ordemDeProducao());
        existing.setTipo(requestDTO.tipo());
        existing.setCorTampa(requestDTO.corTampa());
        Pedido saved = pedidoRepository.save(existing);

        createBlocks(saved, requestDTO.blocos());

        return PedidoMapper.mapDto(pedidoRepository.findById(saved.getId()).get());
    }


    private void validatePedidoForUpdate(PedidoRequestDTO requestDTO, Long currentId) {
        validateRequiredFields(requestDTO);
      
        if (existing.getStatus() != StatusPedido.PENDENTE)
            throw new PedidoNaoPendenteException("atualizar", existing.getStatus());

        if (requestDTO.ordemDeProducao() <= 0)
            throw new InvalidOrdemDeProducaoException(requestDTO.ordemDeProducao());

        if (requestDTO.tipo().getValue() != requestDTO.blocos().size())
            throw new TipoIncompativelComBlocosException(requestDTO.tipo(), requestDTO.blocos().size());
      
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

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findPedidosByExpedicao(Long idExpedicao) {
        return pedidoRepository.findByExpedicaoId(idExpedicao).stream().map(p -> PedidoMapper.mapDto(p)).toList();
    }
}
