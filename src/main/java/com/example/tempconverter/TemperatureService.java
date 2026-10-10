package com.example.tempconverter;

import org.springframework.stereotype.Service;

/**
 * Regras de conversao entre Celsius e Fahrenheit.
 * Valida limites fisicos (zero absoluto) para gerar casos de erro esperados.
 */
@Service
public class TemperatureService {

    private static final double ZERO_ABSOLUTO_CELSIUS = -273.15;
    private static final double ZERO_ABSOLUTO_FAHRENHEIT = -459.67;

    public double celsiusParaFahrenheit(double celsius) {
        if (celsius < ZERO_ABSOLUTO_CELSIUS) {
            throw new IllegalArgumentException(
                "Temperatura abaixo do zero absoluto (-273.15C) e fisicamente impossivel");
        }
        return celsius * 9.0 / 5.0 + 32;
    }

    public double fahrenheitParaCelsius(double fahrenheit) {
        if (fahrenheit < ZERO_ABSOLUTO_FAHRENHEIT) {
            throw new IllegalArgumentException(
                "Temperatura abaixo do zero absoluto (-459.67F) e fisicamente impossivel");
        }
        return (fahrenheit - 32) * 5.0 / 9.0;
    }
}
