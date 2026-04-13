# ================================
# AWS ECR Push Script (PowerShell)
# ================================

param(
    [string]$AwsAccountId,
    [string]$AwsRegion = "us-east-2"
)

# Verificar parâmetros
if ([string]::IsNullOrEmpty($AwsAccountId)) {
    Write-Host "Erro: AWS_ACCOUNT_ID não fornecido" -ForegroundColor Red
    Write-Host "Uso: .\deploy-aws.ps1 -AwsAccountId <ID> -AwsRegion <REGION>" -ForegroundColor Yellow
    Write-Host "Exemplo: .\deploy-aws.ps1 -AwsAccountId 123456789012 -AwsRegion us-east-2" -ForegroundColor Yellow
    exit 1
}

# Configurações
$REPOSITORY_NAME = "personalfood"
$IMAGE_NAME = "personalfood"
$IMAGE_TAG = "latest"
$ECR_REGISTRY = "$AwsAccountId.dkr.ecr.$AwsRegion.amazonaws.com"
$ECR_REPOSITORY = "$ECR_REGISTRY/$REPOSITORY_NAME"
$IMAGE_URI = "$ECR_REPOSITORY`:$IMAGE_TAG"

Write-Host "================================" -ForegroundColor Yellow
Write-Host "AWS ECR Deployment Script" -ForegroundColor Yellow
Write-Host "================================" -ForegroundColor Yellow
Write-Host "AWS Account: $AwsAccountId"
Write-Host "AWS Region: $AwsRegion"
Write-Host "ECR Repository: $ECR_REPOSITORY"
Write-Host ""

# Step 1: Login no ECR
Write-Host "[1/5] Fazendo login no ECR..." -ForegroundColor Yellow
$loginCmd = "aws ecr get-login-password --region $AwsRegion | docker login --username AWS --password-stdin $ECR_REGISTRY"
Invoke-Expression $loginCmd
if ($LASTEXITCODE -ne 0) {
    Write-Host "Erro ao fazer login no ECR" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Login bem-sucedido" -ForegroundColor Green

# Step 2: Criar repositório ECR se não existir
Write-Host "[2/5] Verificando repositório ECR..." -ForegroundColor Yellow
$repoExists = aws ecr describe-repositories --repository-names $REPOSITORY_NAME --region $AwsRegion 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Criando repositório $REPOSITORY_NAME..."
    aws ecr create-repository --repository-name $REPOSITORY_NAME --region $AwsRegion
}
Write-Host "✓ Repositório verificado" -ForegroundColor Green

# Step 3: Build da imagem Docker
Write-Host "[3/5] Compilando imagem Docker..." -ForegroundColor Yellow
docker build -t "$IMAGE_NAME`:$IMAGE_TAG" .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Erro ao compilar imagem Docker" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Imagem compilada com sucesso" -ForegroundColor Green

# Step 4: Tag da imagem
Write-Host "[4/5] Tagging da imagem..." -ForegroundColor Yellow
docker tag "$IMAGE_NAME`:$IMAGE_TAG" $IMAGE_URI
Write-Host "✓ Imagem tagueada" -ForegroundColor Green

# Step 5: Push para ECR
Write-Host "[5/5] Enviando imagem para ECR..." -ForegroundColor Yellow
docker push $IMAGE_URI
if ($LASTEXITCODE -ne 0) {
    Write-Host "Erro ao enviar imagem para ECR" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Imagem enviada com sucesso" -ForegroundColor Green

Write-Host ""
Write-Host "================================" -ForegroundColor Green
Write-Host "Deploy concluído com sucesso!" -ForegroundColor Green
Write-Host "================================" -ForegroundColor Green
Write-Host ""
Write-Host "URI da imagem: $IMAGE_URI" -ForegroundColor Cyan
Write-Host ""
Write-Host "Próximas etapas:"
Write-Host "1. Criar uma Task Definition no ECS com esta imagem"
Write-Host "2. Criar um Service que use essa Task Definition"
Write-Host "3. Configurar o Load Balancer"
Write-Host ""
