# Revisão da IA sobre os testes (TemperatureServiceTest)

Revisão feita pelo Claude sobre a suíte de testes de `TemperatureService`.

## O que os testes atuais já cobrem bem

- Conversão correta em valores comuns (0°C, 100°C, 212°F, 32°F).
- Conversão com valor negativo dentro do limite físico (-40°C, que curiosamente é igual em Fahrenheit).
- Validação de erro para temperaturas abaixo do zero absoluto, nas duas direções.

## Testes que eu sugiro adicionar

**Valores exatamente no limite (teste de fronteira).** Testar `celsiusParaFahrenheit(-273.15)` — esse valor deve ser aceito (é o zero absoluto exato, não "abaixo" dele). Hoje só existe teste para um valor claramente abaixo do limite (-300), então não sabemos se o método trata o limite exato corretamente (bug comum: usar `<` quando deveria ser `<=`, ou vice-versa).

**Mensagem da exceção.** Os testes de erro só verificam que uma `IllegalArgumentException` foi lançada, mas não verificam o texto da mensagem. Se alguém mudar a mensagem por engano, o teste continua passando mesmo que a mensagem fique errada ou incompreensível para quem usa a tela.

**Valores decimais/fracionários.** Todos os testes atuais usam números inteiros. Vale testar algo como `36.6°C`, que é mais realista (ex: temperatura corporal), para garantir que a conversão com casas decimais está correta.

**Teste no controller, não só no serviço.** Hoje só `TemperatureService` é testado. Vale um teste de integração leve (`@WebMvcTest` ou `@SpringBootTest`) simulando um POST em `/converter` com uma direção inválida (nem "CtoF" nem "FtoC"), para confirmar que a tela mostra a mensagem de erro certa e não quebra.

**Valor de entrada extremo, mas válido.** Testar um valor muito alto (ex: 1000000°C) para confirmar que não há overflow ou comportamento inesperado — pouco provável em `double`, mas é um teste de robustez barato de escrever.

## Resumo

A suíte atual cobre bem o caminho feliz e um caso de erro central (zero absoluto), mas não testa os limites exatos, o conteúdo das mensagens de erro, nem a camada de controller. Adicionar esses casos aumentaria a confiança na cobertura sem exigir muito código extra.
