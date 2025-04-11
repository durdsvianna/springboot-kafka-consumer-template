#!/bin/bash

# Script para enviar uma mensagem de teste para o Kafka

KAFKA_TOPIC="CLIENTES"
BOOTSTRAP_SERVER="localhost:9092"

# Mensagem JSON de exemplo (cliente)
CLIENT_MESSAGE='{
  "id": "123",
  "nome": "João Silva",
  "data_criacao": "2023-01-15",
  "data_alteracao": "2023-02-20",
  "email": "joao.silva@example.com",
  "cpf": "12345678900",
  "dataNascimento": "1985-05-15",
  "telefone": "11999998888",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Apto 45",
    "bairro": "Centro",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "01234567"
  },
  "produtos": [
    {
      "codigo": "P001",
      "nome": "Produto 1",
      "descricao": "Descrição do Produto 1",
      "valor": 99.90,
      "categoria": "Eletrônicos"
    }
  ]
}'

# Verifica se o kafka-console-producer está disponível
if ! command -v kafka-console-producer &> /dev/null; then
    echo "Erro: kafka-console-producer não encontrado. Verifique se o Kafka está instalado corretamente."
    exit 1
fi

# Envia a mensagem
echo "Enviando mensagem para o tópico $KAFKA_TOPIC..."
echo "$CLIENT_MESSAGE" | kafka-console-producer --broker-list $BOOTSTRAP_SERVER --topic $KAFKA_TOPIC

# Verifica o resultado
if [ $? -eq 0 ]; then
    echo "Mensagem enviada com sucesso!"
else
    echo "Erro ao enviar mensagem para o Kafka."
    exit 1
fi

echo "Concluído." 