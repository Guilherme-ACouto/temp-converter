package com.example.tempconverter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TemperatureController.class)
@Import(TemperatureService.class)
class TemperatureControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void telaInicial_retornaPaginaComFormulario() throws Exception {
    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Conversor de Temperatura")));
  }

  @Test
  void converter_celsiusParaFahrenheit_mostraResultado() throws Exception {
    mockMvc.perform(post("/converter")
            .param("valor", "0")
            .param("direcao", "CtoF"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("32")));
  }

  @Test
  void converter_direcaoInvalida_mostraMensagemDeErro() throws Exception {
    mockMvc.perform(post("/converter")
            .param("valor", "10")
            .param("direcao", "XYZ"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("invalida")));
  }

  @Test
  void converter_temperaturaAbaixoDoZeroAbsoluto_mostraMensagemDeErro() throws Exception {
    mockMvc.perform(post("/converter")
            .param("valor", "-300")
            .param("direcao", "CtoF"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("zero absoluto")));
  }
}