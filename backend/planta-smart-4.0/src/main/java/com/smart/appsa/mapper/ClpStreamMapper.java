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
import com.smart.appsa.model.clp.MontagemInfoClp;
import com.smart.appsa.model.enums.Estacao;

public final class ClpStreamMapper {

    private ClpStreamMapper() {
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

    public static MontagemStreamDTO toMontagemDTO(Estacao estacao, MontagemInfoClp info, AppStateConfig state) {
        return MontagemStreamDTO.builder()
                .estacao(estacao.getNome())
                .status(info.getStatus())
                .numeroOP(info.getNumeroOP())
                .ocupado(info.isOcupado())
                .aguardando(info.isAguardando())
                .manual(info.isManual())
                .emergencia(info.isEmergencia())
                .recebidoOp(info.isRecebidoOp())
                .startOP(info.isStartOP())
                .finishOP(info.isFinishOP())
                .cancelOP(info.isCancelOP())
                .statusBancada(state.getStatusMontagem())
                .supervisorioEstoque(info.getSupervisorioEstoque())
                .supervisorioProcesso(info.getSupervisorioProcesso())
                .supervisorioMontagem(info.getSupervisorioMontagem())
                .supervisorioExpedicao(info.getSupervisorioExpedicao())
                .statusBancada(state.getStatusMontagem())
                .build();
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
                s.getRegistroInicioPedido(),
                info.getCorAndar1(),
                info.getPosicaoEstoqueAndar1(),
                info.getCorLamina1Andar1(),
                info.getCorLamina2Andar1(),
                info.getCorLamina3Andar1(),
                info.getPadraoLamina1Andar1(),
                info.getPadraoLamina2Andar1(),
                info.getPadraoLamina3Andar1(),
                info.getProcessamentoAndar1(),
                info.getCorAndar2(),
                info.getPosicaoEstoqueAndar2(),
                info.getCorLamina1Andar2(),
                info.getCorLamina2Andar2(),
                info.getCorLamina3Andar2(),
                info.getPadraoLamina1Andar2(),
                info.getPadraoLamina2Andar2(),
                info.getPadraoLamina3Andar2(),
                info.getProcessamentoAndar2(),
                info.getCorAndar3(),
                info.getPosicaoEstoqueAndar3(),
                info.getCorLamina1Andar3(),
                info.getCorLamina2Andar3(),
                info.getCorLamina3Andar3(),
                info.getPadraoLamina1Andar3(),
                info.getPadraoLamina2Andar3(),
                info.getPadraoLamina3Andar3(),
                info.getProcessamentoAndar3(),
                info.getNumeroPedido(),
                info.getAndares(),
                info.getPosicaoExpedicao());
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
