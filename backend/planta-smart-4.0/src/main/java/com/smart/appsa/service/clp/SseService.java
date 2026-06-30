package com.smart.appsa.service.clp;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.smart.appsa.config.AppStateConfig;
import com.smart.appsa.dto.response.FilaStreamDTO;
import com.smart.appsa.events.FilaAlteradaEvent;
import com.smart.appsa.mapper.ClpStreamMapper;
import com.smart.appsa.model.clp.EstoqueInfoClp;
import com.smart.appsa.model.clp.ExpedicaoInfoClp;
import com.smart.appsa.model.clp.MontagemInfo;
import com.smart.appsa.model.clp.ProcessoInfo;
import com.smart.appsa.model.enums.Estacao;
import com.smart.appsa.service.FilaProducaoService;

// Camada SSE no padrão Observer. É notificada (via {@link #publicar(Estacao)}) ao final de
// cada ciclo de leitura de um CLP, monta o DTO da estação e <b>só emite quando o conteúdo
// mudou</b> em relação ao último ciclo. Uma única conexão pode receber eventos de várias
// estações (multiplexação): cada evento é nomeado pelo {@code nome} da estação.
@Service
public class SseService {

    // Um assinante e o conjunto de estações que ele quer receber.
    private record Subscriber(SseEmitter emitter, Set<Estacao> estacoes) {
    }

    private final List<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    private final Map<Estacao, Object> ultimoDto = new ConcurrentHashMap<>();

    // Assinantes do stream da fila de produção (canal independente das estações).
    private final List<SseEmitter> filaSubscribers = new CopyOnWriteArrayList<>();
    private volatile FilaStreamDTO ultimaFila;

    private final EstoqueInfoClp estoqueInfo;
    private final ProcessoInfo processoInfo;
    private final MontagemInfo montagemInfo;
    private final ExpedicaoInfoClp expedicaoInfo;
    private final AppStateConfig appState;
    private final FilaProducaoService filaProducaoService;

    public SseService(EstoqueInfoClp estoqueInfo, ProcessoInfo processoInfo,
                      MontagemInfo montagemInfo, ExpedicaoInfoClp expedicaoInfo,
                      AppStateConfig appState, FilaProducaoService filaProducaoService) {
        this.estoqueInfo = estoqueInfo;
        this.processoInfo = processoInfo;
        this.montagemInfo = montagemInfo;
        this.expedicaoInfo = expedicaoInfo;
        this.appState = appState;
        this.filaProducaoService = filaProducaoService;
    }
    
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
            case PROCESSO -> ClpStreamMapper.toEstacaoDTO(estacao, processoInfo, appState.getStatusProcesso());
            case MONTAGEM -> ClpStreamMapper.toEstacaoDTO(estacao, montagemInfo, appState.getStatusMontagem());
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

    // ---- Stream da fila de produção (evento nomeado "fila") ----

    // Assina o stream da fila e recebe o snapshot inicial ao conectar.
    public SseEmitter subscribeFila() {
        SseEmitter emitter = new SseEmitter(0L); // sem timeout
        filaSubscribers.add(emitter);

        emitter.onCompletion(() -> filaSubscribers.remove(emitter));
        emitter.onTimeout(() -> { filaSubscribers.remove(emitter); emitter.complete(); });
        emitter.onError(e -> filaSubscribers.remove(emitter));

        FilaStreamDTO dto = filaProducaoService.snapshot();
        ultimaFila = dto;
        enviarFila(emitter, dto);
        return emitter;
    }

    // Reage a qualquer alteração da fila (enfileirar/iniciar/concluir/tick do cronômetro)
    // e só emite quando o snapshot difere do último propagado.
    @EventListener
    public void onFilaAlterada(FilaAlteradaEvent event) {
        publicarFila();
    }

    public void publicarFila() {
        FilaStreamDTO novo = filaProducaoService.snapshot();
        if (Objects.equals(ultimaFila, novo)) {
            return; // nada mudou -> não emite
        }
        ultimaFila = novo;
        for (SseEmitter emitter : filaSubscribers) {
            enviarFila(emitter, novo);
        }
    }

    // Snapshot pontual da fila (para o endpoint REST).
    public FilaStreamDTO getFilaSnapshot() {
        return filaProducaoService.snapshot();
    }

    private void enviarFila(SseEmitter emitter, FilaStreamDTO dto) {
        try {
            synchronized (emitter) {
                emitter.send(SseEmitter.event()
                        .name("fila")
                        .data(dto));
            }
        } catch (Exception ex) {
            filaSubscribers.remove(emitter);
            try {
                emitter.complete();
            } catch (Exception ignore) {
                // emitter já encerrado
            }
        }
    }
}
