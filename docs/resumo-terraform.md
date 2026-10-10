# Resumo — Infraestrutura como Código (Terraform)

Na atividade usamos o Terraform para descrever, em código, a infraestrutura que rodaria o nosso conversor de temperatura na AWS de forma resiliente: em vez de um único servidor (ponto único de falha), descrevemos um grupo de servidores atrás de um Load Balancer, com Auto Scaling Group garantindo que sempre existam pelo menos 2 instâncias no ar.

As peças principais do `main.tf`:

- **Security Group**: libera a porta 80 para entrada (HTTP) e todo tráfego de saída.
- **Launch Template**: a receita de como cada instância EC2 nasce — imagem Amazon Linux mais recente, tipo `t3.micro` (free tier), e um script `user_data` que roda na inicialização.
- **Load Balancer (ALB) + Target Group + Listener**: recebem as conexões e distribuem entre as instâncias saudáveis, verificando a cada 15 segundos se cada uma responde na rota `/`.
- **Auto Scaling Group**: mantém entre 2 e 4 instâncias rodando; se uma cair, outra nasce automaticamente.

O vínculo com o nosso projeto específico (Java/Spring Boot) foi feito sem o Terraform precisar saber nada sobre a linguagem: o `user_data` só instala Git e Java, clona o nosso repositório e executa um `start.sh` que fica na raiz do projeto. É esse `start.sh` que sabe compilar com Maven (`mvn package`) e subir o `.jar` do Spring Boot na porta 80. Essa separação (infraestrutura genérica + script próprio do projeto) foi o que entendemos como o conceito central da atividade: o mesmo `main.tf` serviria para qualquer equipe, independente da linguagem usada, desde que exista um `start.sh` compatível.

**Parte mais difícil:** o maior desafio foi entender a diferença entre o que é *dado* (`data "aws_ami"`, `data "aws_vpc"`) e o que é *recurso* (`resource`) no Terraform — ou seja, entender que algumas declarações apenas consultam algo que já existe na conta AWS (como a VPC padrão ou a imagem mais recente do Amazon Linux), enquanto outras de fato criam infraestrutura nova. Também foi necessário entender por que rodar na porta 80 exige privilégio de root, e por que isso não é um problema aqui, já que o `user_data` roda como root automaticamente na inicialização da instância.
