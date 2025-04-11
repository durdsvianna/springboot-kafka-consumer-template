#!/bin/bash

# Script para simular um servidor CRM usando netcat

PORT=8080
ENDPOINT="/api/v2/clientes"

# Função para formatar a resposta HTTP
format_response() {
    local status=$1
    local body=$2
    
    echo -e "HTTP/1.1 $status\r\nContent-Type: application/json\r\nContent-Length: ${#body}\r\nConnection: close\r\n\r\n$body"
}

# Verifica se o netcat está disponível
if ! command -v nc &> /dev/null; then
    echo "Erro: netcat (nc) não encontrado. Instale-o com 'apt-get install netcat'."
    exit 1
fi

echo "Iniciando servidor mock CRM na porta $PORT"
echo "Endpoint: $ENDPOINT"
echo "Use Ctrl+C para finalizar o servidor"
echo "------------------------------------"

# Loop infinito para aceitar conexões
while true; do
    # Recebe a conexão HTTP, processa e envia a resposta
    nc -l $PORT | while read line; do
        # Log da requisição recebida
        echo "$line" | grep "POST $ENDPOINT" > /dev/null
        
        # Se é uma requisição POST para o endpoint de clientes
        if [ $? -eq 0 ]; then
            echo "Requisição POST recebida para $ENDPOINT"
            
            # Aguarda o corpo da requisição
            content_length=$(cat | grep -i "Content-Length:" | cut -d " " -f 2 | tr -d '\r\n')
            
            if [ -n "$content_length" ]; then
                # Lê o corpo da requisição
                body=$(head -c $content_length)
                echo "Corpo da requisição: $body"
                
                # Prepara a resposta de sucesso
                response=$(format_response "201 Created" '{"status":"success","message":"Cliente criado com sucesso"}')
            else
                # Resposta para corpo ausente
                response=$(format_response "400 Bad Request" '{"status":"error","message":"Corpo da requisição ausente"}')
            fi
        else
            # Resposta para endpoint não encontrado
            response=$(format_response "404 Not Found" '{"status":"error","message":"Endpoint não encontrado"}')
        fi
        
        # Envia a resposta
        echo -e "$response"
        break
    done
done 