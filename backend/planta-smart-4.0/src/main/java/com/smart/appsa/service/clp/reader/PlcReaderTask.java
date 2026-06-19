package com.smart.appsa.service.clp.reader;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.smart.appsa.clpcomm.PlcConnector;
import com.smart.appsa.model.enums.Estacao;

/**
 * Tarefa de leitura de um CLP (o "Subject" do padrão Observer).
 *
 * <p>Lê uma ou mais {@link PlcReadRequest} (Data Blocks), concatena o resultado
 * num único {@code byte[]} e notifica todos os {@link PlcDataObserver} registrados.
 * Unifica os antigos {@code PlcReaderDB} (leitura única) e {@code PlcReaderMultDB}
 * (leitura múltipla): a leitura única é apenas uma lista com um elemento.
 */
public class PlcReaderTask implements Runnable {

    private final PlcConnector plcConnector;
    private final String ip;
    private final Estacao estacao;
    private final List<PlcReadRequest> reads;
    private final List<PlcDataObserver> observers = new CopyOnWriteArrayList<>();

    public PlcReaderTask(PlcConnector plcConnector, String ip, Estacao estacao, List<PlcReadRequest> reads) {
        this.plcConnector = plcConnector;
        this.ip = ip;
        this.estacao = estacao;
        this.reads = List.copyOf(reads);
    }

    public void addObserver(PlcDataObserver observer) {
        observers.add(observer);
    }

    @Override
    public void run() {
        try {
            byte[] data = lerBlocos();
            notificarObservers(data);
        } catch (Exception e) {
            System.err.println("Erro ao ler CLP " + estacao.getNome() + " (" + ip + "): " + e.getMessage());
        }
    }

    private byte[] lerBlocos() throws Exception {
        if (reads.size() == 1) {
            PlcReadRequest req = reads.get(0);
            return plcConnector.readBlock(req.db(), req.offset(), req.size());
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (PlcReadRequest req : reads) {
            byte[] bloco = plcConnector.readBlock(req.db(), req.offset(), req.size());
            buffer.write(bloco, 0, bloco.length);
        }
        return buffer.toByteArray();
    }

    private void notificarObservers(byte[] data) {
        for (PlcDataObserver observer : observers) {
            observer.onData(ip, data);
        }
    }
}
