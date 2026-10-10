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

# Bucket simples para armazenar os relatorios de cobertura gerados pela pipeline
resource "aws_s3_bucket" "relatorios_temp_converter" {
  bucket = "temp-converter-relatorios-cobertura"

  tags = {
    Projeto = "conversor-temperatura"
  }
}

resource "aws_s3_bucket_versioning" "relatorios_temp_converter_versionamento" {
  bucket = aws_s3_bucket.relatorios_temp_converter.id

  versioning_configuration {
    status = "Enabled"
  }
}

output "nome_do_bucket" {
  value = aws_s3_bucket.relatorios_temp_converter.bucket
}
