package com.example.tempconverter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simula lentidao crescente no cenario "lentidao" do laboratorio de Canary
 * (Aula 4). So age quando a variavel de ambiente SIMULAR_LENTIDAO=1, usada
 * pelo workflow canary.yml / pipeline-completo.yml. Classe isolada e
 * excluida da cobertura minima (ver pom.xml): e infraestrutura de teste do
 * pipeline, nao logica de negocio do conversor.
 */
public final class LentidaoSimulator {

    private static final AtomicInteger CONTADOR = new AtomicInteger(0);

    private LentidaoSimulator() {
    }

    public static void simular() {
        if ("1".equals(System.getenv("SIMULAR_LENTIDAO"))) {
            int c = CONTADOR.incrementAndGet();
            try {
                Thread.sleep(40L + 3L * c); // +3ms a cada requisicao
            } catch (InterruptedException excecao) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
