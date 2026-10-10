#!/usr/bin/env bash
# ==============================================================================
# scripts/medir.sh — manda tráfego para as duas versões e mede a saúde de cada uma
# Faz o papel do balanceador de carga + observabilidade. Só usa bash, curl e awk.
#
# Uso:
#   bash scripts/medir.sh URL_ESTAVEL URL_NOVA PCT_NOVA N_REQUISICOES ARQUIVO_SAIDA
#
# Exemplo (200 requisições, 10% delas na versão nova):
#   bash scripts/medir.sh http://127.0.0.1:8001/itens \
#                          http://127.0.0.1:8002/itens 10 200 metricas.json
#
# Saída (JSON):
#   {"pct_nova": 10,
#    "nova":    {"requisicoes": 20,  "erros": 0, "taxa_erro_pct": 0,
#                "latencia_p99_ms": 35.1},
#   "estavel": {"requisicoes": 180, "erros": 0, "taxa_erro_pct": 0,
#                "latencia_p99_ms": 27.8}}
# Erro = resposta HTTP 5xx ou sem resposta.
# ==============================================================================
set -euo pipefail

URL_ESTAVEL=${1:?informe URL_ESTAVEL}
URL_NOVA=${2:?informe URL_NOVA}
PCT=${3:-100}
N=${4:-100}
SAIDA=${5:-metricas.json}

TMP=$(mktemp -d)
: > "$TMP/nova"
: > "$TMP/estavel"

for i in $(seq 0 $((N - 1))); do
  # A requisição i vai para a versão nova quando (i % 100) < PCT: 10% é 10% exato
  if [ $((i % 100)) -lt "$PCT" ]; then URL=$URL_NOVA; ARQ=$TMP/nova
  else URL=$URL_ESTAVEL; ARQ=$TMP/estavel; fi
  R=$(curl -s -o /dev/null --max-time 5 -w '%{http_code} %{time_total}' "$URL" || true)
  echo "${R:-000 5}" >> "$ARQ"
done

resumo() {
  if [ ! -s "$1" ]; then echo null; return; fi
  sort -k2 -n "$1" | awk '
    { n++; if ($1 >= 500 || $1 == 0) e++; t[n] = $2 * 1000 }
    END {
      i = int(n * 0.99 + 0.5); if (i < 1) i = 1; if (i > n) i = n
      printf "{\"requisicoes\":%d,\"erros\":%d,\"taxa_erro_pct\":%.2f,\"latencia_p99_ms\":%.1f}",
             n, e, 100 * e / n, t[i]
    }'
  readonly fin
}

jq -n --argjson pct "$PCT" \
      --argjson nova "$(resumo "$TMP/nova")" \
      --argjson estavel "$(resumo "$TMP/estavel")" \
      '{pct_nova: $pct, nova: $nova, estavel: $estavel}' > "$SAIDA"
rm -rf "$TMP"
cat "$SAIDA"
