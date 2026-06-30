package com.smart.appsa.service.clp;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.smart.appsa.clpcomm.PlcConnectionService;
import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.model.enums.Estacao;
import com.smart.appsa.service.clp.reader.PlcDataObserver;
import com.smart.appsa.service.clp.reader.PlcReaderTask;
import com.smart.appsa.service.clp.reader.StationReadConfig;

// Orquestra as threads de leitura dos CLPs.
//
// <p>Para cada estação solicitada cria um {@link PlcReaderTask} (Subject), registra os
// observadores (o {@code CommService} da estação e o {@link PlcDataStore}) e agenda a
// leitura periódica. Centraliza o ciclo de vida das threads — antes espalhado no
// {@code ClpController}.
@Service
public class ClpReadingService {

    private final PlcConnectionService plcConnectionService;
    private final PlcDataStore dataStore;
    private final SseService sseService;
    private final Map<Estacao, PlcDataObserver> commServices;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
    private final Map<Estacao, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();
    private final Map<Estacao, StationReadConfig> configs = StationReadConfig.padroes();

    public ClpReadingService(PlcConnectionService plcConnectionService,
                             PlcDataStore dataStore,
                             SseService sseService,
                             EstoqueComm estoqueCommService,
                             ProcessoComm processoCommService,
                             MontagemComm montagemCommService,
                             ExpedicaoComm expedicaoCommService) {
        this.plcConnectionService = plcConnectionService;
        this.dataStore = dataStore;
        this.sseService = sseService;
        this.commServices = new EnumMap<>(Estacao.class);
        this.commServices.put(Estacao.ESTOQUE, estoqueCommService);
        this.commServices.put(Estacao.PROCESSO, processoCommService);
        this.commServices.put(Estacao.MONTAGEM, montagemCommService);
        this.commServices.put(Estacao.EXPEDICAO, expedicaoCommService);
    }

    // Inicia (ou ignora, se já em execução) a leitura das estações solicitadas.
    //
    // @return mapa estação → {@code true} se a conexão com o CLP foi estabelecida
    //         (ou já estava em leitura), {@code false} se a conexão falhou.
    //         Nomes inválidos são ignorados e não entram no resultado.
    public Map<Estacao, Boolean> start(Map<String, String> ips) {
        Map<Estacao, Boolean> resultados = new EnumMap<>(Estacao.class);
        ips.forEach((nome, ip) -> {
            Optional<Estacao> estacaoOpt = Estacao.fromNome(nome);
            if (estacaoOpt.isEmpty()) {
                System.err.println("Nome de CLP inválido: " + nome);
                return;
            }
            Estacao estacao = estacaoOpt.get();

            if (futures.containsKey(estacao)) {
                resultados.put(estacao, true); // já está lendo
                return;
            }

            PlcConnector connector = plcConnectionService.getConnection(ip);
            if (connector == null) {
                System.err.println("Erro ao obter conexão com o CLP: " + ip);
                resultados.put(estacao, false);
                return;
            }

            StationReadConfig config = configs.get(estacao);
            PlcReaderTask task = new PlcReaderTask(connector, ip, estacao, config.reads());
            // Ordem importa: o CommService parseia o modelo primeiro; depois guardamos o
            // raw e publicamos no SSE (que lê o modelo já atualizado e emite só se mudou).
            task.addObserver(commServices.get(estacao));
            task.addObserver((origemIp, data) -> {
                dataStore.update(estacao, data);
                sseService.publicar(estacao);
            });

            ScheduledFuture<?> future = executor.scheduleWithFixedDelay(
                    task, 0, config.delayMs(), TimeUnit.MILLISECONDS);
            futures.put(estacao, future);
            resultados.put(estacao, true);
        });
        return resultados;
    }

    // Cancela todas as threads de leitura e encerra as conexões com os CLPs.
    public void stop() {
        futures.forEach((estacao, future) -> {
            future.cancel(true);
            System.out.println("Thread de leitura '" + estacao.getNome() + "' cancelada.");
        });
        futures.clear();
        plcConnectionService.closeAll();
    }
}
