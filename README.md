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
