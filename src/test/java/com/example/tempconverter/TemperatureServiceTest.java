package com.example.tempconverter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TemperatureServiceTest {

    private final TemperatureService service = new TemperatureService();

    // --- Casos de acerto (conversao correta) ---

    @Test
    void celsiusParaFahrenheit_zeroGraus_retornaTrintaEDoisGraus() {
        assertEquals(32.0, service.celsiusParaFahrenheit(0), 0.001);
    }

    @Test
    void celsiusParaFahrenheit_cemGraus_retornaDuzentosEDozeGraus() {
        assertEquals(212.0, service.celsiusParaFahrenheit(100), 0.001);
    }

    @Test
    void celsiusParaFahrenheit_temperaturaNegativa_convertemCorretamente() {
        assertEquals(-40.0, service.celsiusParaFahrenheit(-40), 0.001);
    }

    @Test
    void fahrenheitParaCelsius_duzentosEDozeGraus_retornaCemGraus() {
        assertEquals(100.0, service.fahrenheitParaCelsius(212), 0.001);
    }

    @Test
    void fahrenheitParaCelsius_trintaEDoisGraus_retornaZeroGraus() {
        assertEquals(0.0, service.fahrenheitParaCelsius(32), 0.001);
    }

    // --- Casos de erro (validacao de limite fisico) ---

    @Test
    void celsiusParaFahrenheit_abaixoDoZeroAbsoluto_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> service.celsiusParaFahrenheit(-300));
    }

    @Test
    void fahrenheitParaCelsius_abaixoDoZeroAbsoluto_lancaExcecao() {
        assertThrows(IllegalArgumentException.class,
            () -> service.fahrenheitParaCelsius(-500));
    }
}
