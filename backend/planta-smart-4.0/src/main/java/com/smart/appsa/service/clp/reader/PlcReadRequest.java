package com.smart.appsa.service.clp.reader;

// Descreve uma leitura de um único Data Block do CLP.
// Uma estação pode ser composta por várias destas leituras, concatenadas em ordem.
public record PlcReadRequest(int db, int offset, int size) {
}
