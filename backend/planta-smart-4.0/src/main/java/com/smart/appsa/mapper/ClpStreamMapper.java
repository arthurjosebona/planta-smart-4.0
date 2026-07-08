package com.smart.appsa.mapper;

import java.util.ArrayList;
import java.util.List;

import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.dto.clp.stream.EstoqueStreamDTO;
import com.smart.appsa.dto.clp.stream.ExpedicaoStreamDTO;
import com.smart.appsa.dto.clp.stream.MontagemStreamDTO;
import com.smart.appsa.dto.clp.stream.ProcessoStreamDTO;
import com.smart.appsa.model.clp.EstacaoInfoClp;
import com.smart.appsa.model.clp.EstoqueInfoClp;
import com.smart.appsa.model.clp.ExpedicaoInfoClp;
import com.smart.appsa.model.clp.MontagemInfo;
import com.smart.appsa.model.enums.Estacao;

// Converte os modelos parseados dos CLPs ({@code *InfoClp}) nos DTOs de stream.
public final class ClpStreamMapper {

    private ClpStreamMapper() {
    }

    public static MontagemStreamDTO toMontagemDTO(Estacao estacao, MontagemInfo info, int statusBancada) {
        return new MontagemStreamDTO(
                estacao.getNome(),
                info.getStatus(),
                info.getNumeroOP(),
                info.isOcupado(),
                info.isAguardando(),
                info.isManual(),
                info.isEmergencia(),
                info.isRecebidoOp(),
                info.isStartOP(),
                info.isFinishOP(),
                info.isCancelOP(),
                statusBancada,
                info.getSupervisorioEstoque(),
                info.getSupervisorioProcesso(),
                info.getSupervisorioMontagem(),
                info.getSupervisorioExpedicao()
            );
    }

    public static ProcessoStreamDTO toProcessoDTO(Estacao estacao, EstacaoInfoClp info, int statusBancada) {
        return new ProcessoStreamDTO(
                estacao.getNome(),
                info.getStatus(),
                info.getNumeroOP(),
                info.isOcupado(),
                info.isAguardando(),
                info.isManual(),
                info.isEmergencia(),
                info.isRecebidoOp(),
                info.isStartOP(),
                info.isFinishOP(),
                info.isCancelOP(),
                statusBancada);
    }

    public static EstoqueStreamDTO toEstoqueDTO(EstoqueInfoClp info, AppStateConfig s) {
        return new EstoqueStreamDTO(
                Estacao.ESTOQUE.getNome(),
                info.getStatus(),
                info.getNumeroOP(),
                info.isOcupado(),
                info.isAguardando(),
                info.isManual(),
                info.isEmergencia(),
                info.isIniciarPedido(),
                info.isPedirPosicaoEst(),
                info.isAdicionarEstoque(),
                info.isRemoverEstoque(),
                info.isRetornoEstoqueCheio(),
                info.isRecebidoEstoque(),
                info.isIniciarGuardarEst(),
                info.getPosicaoEstoque(),
                info.getPosicaoGuardarEst(),
                info.getCorGuardarEstoque(),
                toList(info.getPosicoesOcupadas()),
                s.getStatusEstoque(),
                s.getStatusProcesso(),
                s.getStatusMontagem(),
                s.getStatusExpedicao(),
                s.getStatusProducao(),
                s.isPedidoEmCurso(),
                s.getRegistroInicioPedido()
            );
    }

    public static ExpedicaoStreamDTO toExpedicaoDTO(ExpedicaoInfoClp info, AppStateConfig s) {
        return new ExpedicaoStreamDTO(
                Estacao.EXPEDICAO.getNome(),
                info.getStatus(),
                info.getNumeroOP(),
                info.isOcupado(),
                info.isAguardando(),
                info.isManual(),
                info.isEmergencia(),
                info.isPedirPosicaoExp(),
                info.isAdicionarExpedicao(),
                info.isRemoverExpedicao(),
                info.isIniciarGuardarExp(),
                info.isRecebidoExpedicao(),
                info.getPosicaoGuardarExp(),
                info.getPosicaoGuardadoExpedicao(),
                info.getPosicaoRemovidoExpedicao(),
                info.getOpGuardadoExpedicao(),
                toList(info.getOrderExpedicao()),
                s.getStatusExpedicao());
    }

    private static List<Integer> toList(byte[] arr) {
        if (arr == null) {
            return List.of();
        }
        List<Integer> lista = new ArrayList<>(arr.length);
        for (byte b : arr) {
            lista.add(b & 0xFF);
        }
        return lista;
    }

    private static List<Integer> toList(int[] arr) {
        if (arr == null) {
            return List.of();
        }
        List<Integer> lista = new ArrayList<>(arr.length);
        for (int i : arr) {
            lista.add(i);
        }
        return lista;
    }
}
