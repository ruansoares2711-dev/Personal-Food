# 🚀 Guia Completo de Deployment na AWS

## 📋 Pré-requisitos

1. **Conta AWS** ativa com acesso a RDS, ECR e ECS
2. **Docker** instalado e rodando
3. **AWS CLI** instalado e configurado com credenciais
4. **Git** para versionamento

Instalar AWS CLI:
```powershell
# Windows
choco install awscli
# ou
msiexec.exe /i https://awscli.amazonaws.com/AWSCLIV2.msi
```

Configurar credenciais:
```powershell
aws configure
# Forneça:
# AWS Access Key ID: [sua_chave]
# AWS Secret Access Key: [sua_secreta]
# Default region: us-east-2
```

---

## 🗄️ STEP 1: Criar RDS MySQL

### Via AWS Console:

1. Acesse: https://console.aws.amazon.com/rds
2. Clique em **"Create database"**
3. Selecione **MySQL 8.0**
4. Configure:
   - **DB instance identifier**: `personalfood-db`
   - **Master username**: `admin`
   - **Master password**: Uma senha forte (salve!)
   - **DB instance class**: `db.t3.micro` (gratuito por 12 meses)
   - **Storage**: 20 GB `gp3`
   - **Multi-AZ**: Não (para economizar)
   - **Public accessibility**: Yes (importante para testes)
   - **Initial database name**: `personal_food`

5. Clique em **"Create database"** e aguarde 5-10 minutos

### Via AWS CLI:

```bash
aws rds create-db-instance \
    --db-instance-identifier personalfood-db \
    --db-instance-class db.t3.micro \
    --engine mysql \
    --engine-version 8.0.35 \
    --master-username admin \
    --master-user-password "SuaSenhaForte123!" \
    --allocated-storage 20 \
    --storage-type gp3 \
    --db-name personal_food \
    --publicly-accessible \
    --region us-east-2
```

**Após criado**, copie o **Endpoint RDS** (algo como: `personalfood-db.c5eucqgwa20t.us-east-2.rds.amazonaws.com`)

---

## 🐳 STEP 2: Build e Push da Imagem Docker

### Opção A: Usando PowerShell (Windows)

```powershell
# 1. Obter seu AWS Account ID
$AWS_ACCOUNT_ID = aws sts get-caller-identity --query Account --output text
Write-Host "Seu AWS Account ID: $AWS_ACCOUNT_ID"

# 2. Executar script de deployment
.\deploy-aws.ps1 -AwsAccountId $AWS_ACCOUNT_ID -AwsRegion us-east-2
```

### Opção B: Usando Bash (Linux/Mac)

```bash
# 1. Obter seu AWS Account ID
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
echo "Seu AWS Account ID: $AWS_ACCOUNT_ID"

# 2. Executar script de deployment
chmod +x deploy-aws.sh
./deploy-aws.sh $AWS_ACCOUNT_ID us-east-2
```

### Opção C: Manualmente

```powershell
# 1. Fazer login no ECR
$AWS_ACCOUNT_ID = "123456789012"  # Substitua pelo seu
$AWS_REGION = "us-east-2"
$ECR_REGISTRY = "$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"

aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ECR_REGISTRY

# 2. Criar repositório ECR (se não existir)
aws ecr create-repository --repository-name personalfood --region $AWS_REGION

# 3. Build da imagem
docker build -t personalfood:latest .

# 4. Tag da imagem
docker tag personalfood:latest $ECR_REGISTRY/personalfood:latest

# 5. Push para ECR
docker push $ECR_REGISTRY/personalfood:latest
```

---

## 🏗️ STEP 3: Criar ECS Cluster

### Via AWS Console:

1. Acesse: https://console.aws.amazon.com/ecs
2. Clique em **"Clusters"** → **"Create cluster"**
3. Configure:
   - **Cluster name**: `personalfood-cluster`
   - **Infrastructure**: Fargate (padrão)
   - **Default capacity provider**: Fargate
4. Clique em **"Create"**

### Via AWS CLI:

```bash
aws ecs create-cluster --cluster-name personalfood-cluster --region us-east-2
```

---

## 📋 STEP 4: Criar Task Definition

### Via AWS Console:

1. Acesse ECS → **"Task Definitions"** → **"Create new task definition"**
2. Configure:
   - **Task Definition Family**: `personalfood-task`
   - **Infrastructure requirements**:
     - Launch type: Fargate
     - OS: Linux
     - CPU: 0.5 vCPU
     - Memory: 1 GB
   
3. Clique em **"Add container"**:
   - **Container name**: `personalfood`
   - **Image URI**: `123456789012.dkr.ecr.us-east-2.amazonaws.com/personalfood:latest` (substitua o account ID)
   - **Port mapping**: 8080 (TCP)
   - **Environment variables**: (ver abaixo)

### Environment Variables:

```
DB_HOST=personalfood-db.c5eucqgwa20t.us-east-2.rds.amazonaws.com
DB_PORT=3306
DB_NAME=personal_food
DB_USER=admin
DB_PASSWORD=SuaSenhaForte123!
MP_TOKEN=seu_token_mercadopago
EMAIL_PASSWORD=sua_senha_app_gmail
GEMINI_API_KEY=sua_chave_gemini
GOOGLE_CLIENT_ID=seu_client_id
GOOGLE_CLIENT_SECRET=seu_client_secret
```

4. Clique em **"Create"**

---

## 🚀 STEP 5: Criar ECS Service

### Via AWS Console:

1. Acesse ECS → **Clusters** → `personalfood-cluster`
2. Clique em **"Create service"**
3. Configure:
   - **Launch type**: Fargate
   - **Task Definition**: `personalfood-task`
   - **Service name**: `personalfood-service`
   - **Desired tasks**: 1
   - **Deployment configuration**: Rolling update

4. **Networking**:
   - **VPC**: Padrão
   - **Subnets**: Selecione pelo menos 2
   - **Security groups**: Crie uma nova ou selecione existente

5. **Load Balancing** (importante):
   - **Load balancer type**: Application Load Balancer
   - **Load balancer name**: Crie novo → `personalfood-alb`
   - **Container port**: 8080
   - **Listener port**: 80 (HTTP) ou 443 (HTTPS)

6. Clique em **"Create service"**

---

## ✅ STEP 6: Verificar Deployment

```powershell
# Obter URL do Load Balancer
aws elbv2 describe-load-balancers `
    --region us-east-2 `
    --query 'LoadBalancers[?LoadBalancerName==`personalfood-alb`].DNSName' `
    --output text

# Resultado será algo como:
# personalfood-alb-1234567890.us-east-2.elb.amazonaws.com
```

Acesse no navegador: `http://personalfood-alb-xxxxx.us-east-2.elb.amazonaws.com`

---

## 🔒 STEP 7: Configurar HTTPS (Recomendado)

1. Obtenha um certificado SSL via **AWS Certificate Manager**
2. Adicione HTTPS ao Load Balancer
3. Redirecione HTTP → HTTPS

---

## 📊 Monitoramento

Ver logs:
```powershell
aws logs tail /ecs/personalfood-task --follow --region us-east-2
```

Ver métricas:
```powershell
aws cloudwatch get-metric-statistics `
    --namespace AWS/ECS `
    --metric-name CPUUtilization `
    --dimensions Name=ServiceName,Value=personalfood-service Name=ClusterName,Value=personalfood-cluster `
    --start-time (Get-Date).AddHours(-1) `
    --end-time (Get-Date) `
    --period 60 `
    --statistics Average `
    --region us-east-2
```

---

## 💰 Custos Estimados (Mensal)

| Serviço | Custo |
|---------|-------|
| RDS MySQL (db.t3.micro, 20GB) | ~$10 |
| ECS Fargate (0.5 vCPU, 1GB) | ~$15 |
| Application Load Balancer | ~$20 |
| Data Transfer (assumindo <100GB) | ~$5 |
| **Total** | **~$50/mês** |

*Nota: Free tier da AWS cobre 12 meses parcialmente*

---

## 🐛 Troubleshooting

### Erro: "Unable to pull image"
- Verifique se o ECR tem a imagem
- Verifique permissões de IAM
- Verifique credenciais AWS

### Erro: "Database connection refused"
- Verifique se o RDS está em status "Available"
- Verifique se o security group permite porta 3306
- Verifique credenciais do banco de dados

### Aplicação não inicia
- Veja os logs: `aws logs tail /ecs/personalfood-task --follow`
- Verifique environment variables
- Verifique se o banco de dados está criado

---

## 🎯 Checklist Final

- [ ] RDS MySQL criado e rodando
- [ ] ECR repository criado
- [ ] Imagem Docker compilada e enviada para ECR
- [ ] ECS Cluster criado
- [ ] Task Definition criada
- [ ] Service criado
- [ ] Load Balancer funcionando
- [ ] Aplicação acessível via URL
- [ ] SSL/HTTPS configurado
- [ ] Backups de banco de dados configurados
- [ ] Monitoramento ativado

---

## 📞 Suporte

Se tiver problemas, verifique:
1. **Logs do ECS**: Console AWS → ECS → Tasks → Logs
2. **Logs do RDS**: Console AWS → RDS → Databases → Logs
3. **CloudWatch**: Para métricas e alarmes

Boa sorte! 🚀
