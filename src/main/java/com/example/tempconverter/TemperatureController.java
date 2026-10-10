package com.example.tempconverter;

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

    public TemperatureController(TemperatureService temperatureService) {
        this.temperatureService = temperatureService;
    }

    @GetMapping("/")
    public String index(Model model) {
        LentidaoSimulator.simular();
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
