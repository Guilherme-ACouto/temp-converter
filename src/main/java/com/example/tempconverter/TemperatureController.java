package com.example.tempconverter;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TemperatureController {

    private static final String CTO_F = "CtoF";
    private static final String FTO_C = "FtoC";

    private final TemperatureService temperatureService;

    // Contador global usado para simular lentidão crescente no cenário "lentidao"
    // do laboratorio de Canary (Aula 4). So age quando SIMULAR_LENTIDAO=1.
    private static final AtomicInteger CONTADOR_LENTIDAO = new AtomicInteger(0);

    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    private void simularLentidao() {
        if ("1".equals(System.getenv("SIMULAR_LENTIDAO"))) {
            int c = CONTADOR_LENTIDAO.incrementAndGet();
            try {
                Thread.sleep(40L + 3L * c); // +3ms a cada requisicao
            } catch (InterruptedException excecao) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @GetMapping("/")
    public String index(Model model) {
        simularLentidao();
        model.addAttribute("direcao", CTO_F);
        return "index";
    }

    @PostMapping("/converter")
    public String converter(@RequestParam double valor,
        @RequestParam String direcao,
        Model model) {
        model.addAttribute("valor", valor);
        model.addAttribute("direcao", direcao);

        try {
            double resultado;
            if (CTO_F.equals(direcao)) {
                resultado = temperatureService.celsiusParaFahrenheit(valor);
            } else if (FTO_C.equals(direcao)) {
                resultado = temperatureService.fahrenheitParaCelsius(valor);
            } else {
                throw new IllegalArgumentException("Direcao de conversao invalida");
            }
            model.addAttribute("resultado", resultado);
        } catch (IllegalArgumentException excecao) {
            model.addAttribute("erro", excecao.getMessage());
        }

        return "index";
    }
}
