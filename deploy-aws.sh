#!/bin/bash

# ================================
# AWS ECR Push Script
# ================================

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configurações
AWS_ACCOUNT_ID="$1"
AWS_REGION="${2:-us-east-2}"
REPOSITORY_NAME="personalfood"
IMAGE_NAME="personalfood"
IMAGE_TAG="latest"

if [ -z "$AWS_ACCOUNT_ID" ]; then
    echo -e "${RED}Erro: AWS_ACCOUNT_ID não fornecido${NC}"
    echo "Uso: ./deploy-aws.sh <AWS_ACCOUNT_ID> [AWS_REGION]"
    echo "Exemplo: ./deploy-aws.sh 123456789012 us-east-2"
    exit 1
fi

ECR_REGISTRY="${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
ECR_REPOSITORY="${ECR_REGISTRY}/${REPOSITORY_NAME}"
IMAGE_URI="${ECR_REPOSITORY}:${IMAGE_TAG}"

echo -e "${YELLOW}================================${NC}"
echo -e "${YELLOW}AWS ECR Deployment Script${NC}"
echo -e "${YELLOW}================================${NC}"
echo "AWS Account: $AWS_ACCOUNT_ID"
echo "AWS Region: $AWS_REGION"
echo "ECR Repository: $ECR_REPOSITORY"
echo ""

# Step 1: Login no ECR
echo -e "${YELLOW}[1/5] Fazendo login no ECR...${NC}"
aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}
if [ $? -ne 0 ]; then
    echo -e "${RED}Erro ao fazer login no ECR${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Login bem-sucedido${NC}"

# Step 2: Criar repositório ECR se não existir
echo -e "${YELLOW}[2/5] Verificando repositório ECR...${NC}"
aws ecr describe-repositories --repository-names ${REPOSITORY_NAME} --region ${AWS_REGION} 2>/dev/null
if [ $? -ne 0 ]; then
    echo "Criando repositório ${REPOSITORY_NAME}..."
    aws ecr create-repository --repository-name ${REPOSITORY_NAME} --region ${AWS_REGION}
fi
echo -e "${GREEN}✓ Repositório verificado${NC}"

# Step 3: Build da imagem Docker
echo -e "${YELLOW}[3/5] Compilando imagem Docker...${NC}"
docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
if [ $? -ne 0 ]; then
    echo -e "${RED}Erro ao compilar imagem Docker${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Imagem compilada com sucesso${NC}"

# Step 4: Tag da imagem
echo -e "${YELLOW}[4/5] Tagging da imagem...${NC}"
docker tag ${IMAGE_NAME}:${IMAGE_TAG} ${IMAGE_URI}
echo -e "${GREEN}✓ Imagem tagueada${NC}"

# Step 5: Push para ECR
echo -e "${YELLOW}[5/5] Enviando imagem para ECR...${NC}"
docker push ${IMAGE_URI}
if [ $? -ne 0 ]; then
    echo -e "${RED}Erro ao enviar imagem para ECR${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Imagem enviada com sucesso${NC}"

echo ""
echo -e "${GREEN}================================${NC}"
echo -e "${GREEN}Deploy concluído com sucesso!${NC}"
echo -e "${GREEN}================================${NC}"
echo ""
echo "URI da imagem: ${IMAGE_URI}"
echo ""
echo "Próximas etapas:"
echo "1. Criar uma Task Definition no ECS com esta imagem"
echo "2. Criar um Service que use essa Task Definition"
echo "3. Configurar o Load Balancer"
echo ""
