terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "us-east-1"
}

# ---------------------------------------------------------------------------
# Vinculo com o projeto: o Terraform nao sabe que a aplicacao e Java/Maven,
# ele so clona o repositorio e executa o start.sh que esta na raiz dele.
# ---------------------------------------------------------------------------
variable "repo_url" {
  description = "URL do repositorio Git do projeto (deve conter um start.sh na raiz)"
  type        = string
  default     = "https://github.com/Guilherme-ACouto/temp-converter.git"
}

variable "repo_branch" {
  description = "Branch do repositorio usada no deploy"
  type        = string
  default     = "main"
}

# Busca automaticamente a imagem mais recente do Amazon Linux,
# em vez de fixar um ID de AMI que pode ficar desatualizado.
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]
  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

resource "aws_security_group" "temp_converter_sg" {
  name        = "temp-converter-sg"
  description = "Permite trafego HTTP para o conversor de temperatura"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Script que roda automaticamente quando cada instancia EC2 nasce.
# Instala Java e Git, clona o repositorio do projeto e executa o start.sh
# dele, que sabe compilar com Maven e subir a aplicacao Spring Boot na porta 80.
locals {
  user_data_script = <<-EOF
    #!/bin/bash
    # 1) Instala Git e Java 17 (o projeto usa Spring Boot 3 / Java 17).
    yum install -y git java-17-amazon-corretto

    # 2) Baixa o codigo-fonte do projeto a partir do repositorio e branch
    #    informados nas variaveis. Isso "vincula" a infraestrutura ao projeto.
    git clone --branch ${var.repo_branch} ${var.repo_url} /app
    cd /app

    # 3) Da permissao de execucao ao script de inicializacao do projeto.
    #    O start.sh sabe compilar com Maven e subir o Spring Boot na porta 80.
    chmod +x start.sh

    # 4) Executa o start.sh em segundo plano (nohup + &), para que a
    #    aplicacao continue rodando mesmo depois que o user_data terminar.
    nohup ./start.sh > /var/log/app.log 2>&1 &
  EOF
}

resource "aws_launch_template" "temp_converter_server" {
  name_prefix            = "temp-converter-"
  image_id               = data.aws_ami.amazon_linux.id
  instance_type          = "t3.micro"
  vpc_security_group_ids = [aws_security_group.temp_converter_sg.id]
  user_data              = base64encode(local.user_data_script)
}

resource "aws_lb" "temp_converter_lb" {
  name               = "temp-converter-lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.temp_converter_sg.id]
  subnets            = data.aws_subnets.default.ids
}

resource "aws_lb_target_group" "temp_converter_tg" {
  name     = "temp-converter-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = data.aws_vpc.default.id

  health_check {
    path                = "/"
    interval            = 15
    healthy_threshold   = 2
    unhealthy_threshold = 2
  }
}

resource "aws_lb_listener" "temp_converter_listener" {
  load_balancer_arn = aws_lb.temp_converter_lb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.temp_converter_tg.arn
  }
}

resource "aws_autoscaling_group" "temp_converter_asg" {
  name                = "temp-converter-asg"
  min_size            = 2
  max_size            = 4
  desired_capacity    = 2
  vpc_zone_identifier = data.aws_subnets.default.ids
  target_group_arns   = [aws_lb_target_group.temp_converter_tg.arn]
  health_check_type   = "ELB"

  launch_template {
    id      = aws_launch_template.temp_converter_server.id
    version = "$Latest"
  }

  tag {
    key                 = "Name"
    value               = "temp-converter-server"
    propagate_at_launch = true
  }
}

output "temp_converter_url" {
  value = "http://${aws_lb.temp_converter_lb.dns_name}"
}
