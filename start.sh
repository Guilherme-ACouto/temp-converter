#!/bin/bash
# start.sh - instala dependencias e sobe o conversor de temperatura na porta 80.
# E executado automaticamente pelo user_data do Terraform (terraform/main.tf),
# depois que a instancia EC2 clona este repositorio.
set -e

# Maven nao vem instalado no Amazon Linux por padrao
yum install -y maven

# Compila o projeto (gera o .jar em target/)
mvn -B -q -DskipTests package

# Sobe o Spring Boot na porta 80 (a porta liberada pelo Security Group
# e usada pelo Load Balancer). Rodar na porta 80 exige root, o que ja
# e o caso aqui pois o user_data roda como root.
JAR=$(find target -maxdepth 1 -name "*.jar" ! -name "*.original" | head -n1)
java -jar "$JAR" --server.port=80
