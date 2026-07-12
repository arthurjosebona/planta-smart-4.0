package com.smart.appsa.service.clp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.mapper.ClpStreamMapper;
import com.smart.appsa.model.clp.EstoqueInfoClp;
import com.smart.appsa.model.clp.ExpedicaoInfoClp;
import com.smart.appsa.model.clp.MontagemInfoClp;
import com.smart.appsa.model.clp.ProcessoInfoClp;
import com.smart.appsa.model.enums.Estacao;

import lombok.RequiredArgsConstructor;

// Camada SSE no padrão Observer. É notificada (via {@link #publicar(Estacao)}) ao final de
// cada ciclo de leitura de um CLP, monta o DTO da estação e <b>só emite quando o conteúdo
// mudou</b> em relação ao último ciclo. Uma única conexão pode receber eventos de várias
// estações (multiplexação): cada evento é nomeado pelo {@code nome} da estação.
@Service
@RequiredArgsConstructor
public class SseService {

    // Um assinante e o conjunto de estações que ele quer receber.
    private record Subscriber(SseEmitter emitter, Set<Estacao> estacoes) {
    }

    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    private final Map<Estacao, Object> ultimoDto = new ConcurrentHashMap<>();

    private final EstoqueInfoClp estoqueInfo;
    private final ProcessoInfoClp processoInfo;
    private final MontagemInfoClp montagemInfo;
    private final ExpedicaoInfoClp expedicaoInfo;
    private final AppStateConfig appState;

    public SseEmitter subscribe(Set<Estacao> estacoes) {
        SseEmitter emitter = new SseEmitter(0L); // sem timeout
        Subscriber sub = new Subscriber(emitter, Set.copyOf(estacoes));
        subscribers.add(sub);

        emitter.onCompletion(() -> subscribers.remove(sub));
        emitter.onTimeout(() -> { subscribers.remove(sub); emitter.complete(); });
        emitter.onError(e -> subscribers.remove(sub));

        // Snapshot inicial: garante que o cliente receba o estado corrente ao conectar.
        for (Estacao estacao : sub.estacoes()) {
            Object dto = ultimoDto.computeIfAbsent(estacao, this::build);
            enviar(sub, estacao, dto);
        }
        return emitter;
    }

    // Notifica a chegada de novos dados de uma estação. Monta o DTO e só propaga aos
    // assinantes se ele difere do último emitido para essa estação.
    public void publicar(Estacao estacao) {
        Object novo = build(estacao);
        Object anterior = ultimoDto.put(estacao, novo);
        if (Objects.equals(anterior, novo)) {
            return; // nenhum campo mudou -> não emite
        }
        for (Subscriber sub : subscribers) {
            if (sub.estacoes().contains(estacao)) {
                enviar(sub, estacao, novo);
            }
        }
    }

    private Object build(Estacao estacao) {
        return switch (estacao) {
            case ESTOQUE -> ClpStreamMapper.toEstoqueDTO(estoqueInfo, appState);
            case PROCESSO -> ClpStreamMapper.toProcessoDTO(estacao, processoInfo, appState.getStatusProcesso());
            case MONTAGEM -> ClpStreamMapper.toMontagemDTO(estacao, montagemInfo, appState);
            case EXPEDICAO -> ClpStreamMapper.toExpedicaoDTO(expedicaoInfo, appState);
        };
    }

    private void enviar(Subscriber sub, Estacao estacao, Object dto) {
        try {
            // Sincroniza por emitter: várias threads de estação podem escrever no mesmo cliente.
            synchronized (sub.emitter()) {
                sub.emitter().send(SseEmitter.event()
                        .name(estacao.getNome())
                        .data(dto));
            }
        } catch (Exception ex) {
            subscribers.remove(sub);
            try {
                sub.emitter().complete();
            } catch (Exception ignore) {
                // emitter já encerrado
            }
        }
    }
}
