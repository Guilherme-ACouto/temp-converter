# Conversor de Temperatura (Celsius / Fahrenheit)

Projeto Java + Spring Boot com tela web simples, linter (PMD), testes com cobertura (JaCoCo), pipeline de CI e um Terraform pequeno.

## Rodando localmente

```
mvn spring-boot:run
```

Depois acesse `http://localhost:8080` no navegador.

## Rodando os testes com cobertura

```
mvn verify
```

O relatório de cobertura fica em `target/site/jacoco/index.html` — abra esse arquivo no navegador.

## Rodando o linter (PMD)

```
mvn pmd:check
```

Se houver violação de regra, o build falha e mostra o arquivo/linha do problema.

## Terraform

Dentro da pasta `terraform/`:

```
terraform init
terraform plan
```

Isso é só demonstrativo (cria um bucket S3 simbólico para guardar relatórios de cobertura). Não precisa aplicar (`terraform apply`) para a atividade — o `plan` já mostra que o código é válido.

## Estrutura

- `src/main/java` — código da aplicação (serviço de conversão + controller da tela)
- `src/main/resources/templates/index.html` — a tela
- `src/test/java` — testes (casos de sucesso e casos de erro)
- `pmd-ruleset.xml` — regras do linter
- `.github/workflows/ci.yml` — pipeline: roda PMD e os testes em todo PR
- `terraform/main.tf` — infraestrutura mínima de exemplo
- `revisao-ia-testes.md` — revisão da IA sobre os testes, com sugestões de testes adicionais

## Limitações e ética (Laboratório Aula 4)

- **Dados sensíveis enviados à IA:** apenas logs de aplicação (mensagens de erro técnicas, sem dados pessoais) e métricas de performance (latência, taxa de erro). Nenhum dado de usuário real, senha ou informação sensível é enviado aos prompts.
- **Se a IA errar ou alucinar:** toda decisão da IA passa por uma "opção segura" fixa (fail-safe) quando a resposta vem fora do formato esperado, e por uma regra fixa determinística que roda em paralelo e nunca depende da IA (ex: `grep "^CRITICAL"` nos logs, ou o limite fixo de erro/latência no canary). A IA nunca é a única camada de decisão.
- **Decisão que nunca deve ficar só com a IA, e por quê:** a aprovação final de deploy em produção (`environment: producao`, com revisor humano obrigatório). A IA pode errar, ser enganada (prompt injection) ou ficar indisponível; uma decisão irreversível como "colocar código novo no ar" precisa sempre de confirmação humana.
- **O ataque de prompt injection (Tarefa 4) passou em alguma versão?** Não. No pipeline antigo (`log-analysis.yml`), a própria IA reconheceu a tentativa de override no log e respondeu `CRITICO` mesmo assim. No pipeline defendido (`log-analysis-defendido.yml`), a resposta da IA veio fora do formato esperado e a opção segura (`CRITICO`) foi aplicada automaticamente — ou seja, mesmo que a IA fosse enganada, a camada de fail-safe e a regra fixa (`grep "^CRITICAL"`, independente da IA) garantem que o alerta nunca seria perdido.
- **Limitação conhecida da solução:** a `ia_decisao.sh` depende de uma API externa (GroqCloud); se o serviço estiver fora do ar ou lento, o pipeline sempre cai na opção segura — o que é seguro, mas pode gerar falsos positivos (ex: bloquear um deploy que na verdade estava ok) com mais frequência do que o ideal.
