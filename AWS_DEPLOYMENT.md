# AWS Deployment Guide - PersonalFood

## 📋 Pré-requisitos

- ✅ AWS Account (com permissões de EC2, RDS, ECS, IAM)
- ✅ AWS CLI configurado localmente
- ✅ Docker instalado
- ✅ GitHub account (para CI/CD)

## 🚀 Opção 1: Deployment no AWS EC2 (Recomendado para iniciantes)

### Passo 1: Criar EC2 Instance

```bash
# Via AWS Console:
1. EC2 Dashboard → Instances → Launch Instance
2. Escolher: Ubuntu Server 24.04 LTS (Free Tier eligible)
3. Instance type: t3.micro (Free Tier) ou t3.small (melhor performance)
4. Security Group:
   - SSH (22) - seu IP
   - HTTP (80) - 0.0.0.0/0
   - HTTPS (443) - 0.0.0.0/0
   - Custom TCP (8080) - 0.0.0.0/0
5. Storage: 30GB gp3
```

### Passo 2: Conectar à EC2 e Instalar Dependências

```bash
# SSH na instância
ssh -i sua-chave.pem ubuntu@seu-elastic-ip

# Atualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar Docker
sudo apt install -y docker.io docker-compose
sudo usermod -aG docker $USER
newgrp docker

# Verificar instalação
docker --version
docker-compose --version
```

### Passo 3: Deploy da Aplicação

```bash
# Clonar repositório
git clone https://github.com/ruansoares2711-dev/Personal-Food.git
cd Personal-Food

# Criar arquivo .env com as variáveis (IMPORTANTE!)
cat > .env << EOF
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/personal_food
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=SuaSenhaForte123!
MP_TOKEN=seu-mercado-pago-token
EMAIL_PASSWORD=sua-senha-gmail
GEMINI_API_KEY=sua-api-key
GOOGLE_CLIENT_ID=seu-client-id
GOOGLE_CLIENT_SECRET=seu-client-secret
EOF

# Iniciar containers (Docker Compose)
docker-compose up -d

# Verificar status
docker-compose ps
docker-compose logs -f app
```

### Passo 4: Configurar Nginx Reverse Proxy (Opcional)

```bash
# Instalar Nginx
sudo apt install -y nginx

# Criar arquivo de config
sudo nano /etc/nginx/sites-available/personalfood

# Adicionar:
upstream app {
    server localhost:8080;
}

server {
    listen 80;
    server_name seu-dominio.com www.seu-dominio.com;

    location / {
        proxy_pass http://app;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# Ativar config
sudo ln -s /etc/nginx/sites-available/personalfood /etc/nginx/sites-enabled/
sudo systemctl restart nginx
```

---

## 🐳 Opção 2: Deployment no AWS ECS (Elastic Container Service)

### Passo 1: Criar Repository no ECR (Elastic Container Registry)

```bash
# Criar repository
aws ecr create-repository --repository-name personalfood --region us-east-1

# Login no ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin {seu-account-id}.dkr.ecr.us-east-1.amazonaws.com

# Build e push da imagem
docker build -t personalfood:latest .
docker tag personalfood:latest {seu-account-id}.dkr.ecr.us-east-1.amazonaws.com/personalfood:latest
docker push {seu-account-id}.dkr.ecr.us-east-1.amazonaws.com/personalfood:latest
```

### Passo 2: Criar RDS (PostgreSQL Database)

```bash
# Via AWS Console:
1. RDS Dashboard → Databases → Create database
2. Engine: PostgreSQL 15
3. Tier: db.t3.micro (Free Tier)
4. DB instance identifier: personalfood-db
5. Master username: postgres
6. Password: SuaSenhaForte123!
7. Initial database name: personal_food
8. Security Group: Permitir acesso da EC2/ECS
```

### Passo 3: Criar Task Definition no ECS

```json
{
  "family": "personalfood",
  "networkMode": "awsvpc",
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "256",
  "memory": "512",
  "containerDefinitions": [
    {
      "name": "personalfood",
      "image": "{seu-account-id}.dkr.ecr.us-east-1.amazonaws.com/personalfood:latest",
      "portMappings": [
        {
          "containerPort": 8080,
          "hostPort": 8080,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_DATASOURCE_URL",
          "value": "jdbc:postgresql://seu-rds-endpoint:5432/personal_food"
        },
        {
          "name": "SPRING_DATASOURCE_USERNAME",
          "value": "postgres"
        }
      ],
      "secrets": [
        {
          "name": "SPRING_DATASOURCE_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:{account-id}:secret:personalfood/db-password"
        },
        {
          "name": "MP_TOKEN",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:{account-id}:secret:personalfood/mp-token"
        },
        {
          "name": "EMAIL_PASSWORD",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:{account-id}:secret:personalfood/email-password"
        },
        {
          "name": "GEMINI_API_KEY",
          "valueFrom": "arn:aws:secretsmanager:us-east-1:{account-id}:secret:personalfood/gemini-key"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "/ecs/personalfood",
          "awslogs-region": "us-east-1",
          "awslogs-stream-prefix": "ecs"
        }
      }
    }
  ]
}
```

### Passo 4: Criar ECS Cluster e Service

```bash
# Criar Cluster
aws ecs create-cluster --cluster-name personalfood-cluster --region us-east-1

# Criar Service
aws ecs create-service \
  --cluster personalfood-cluster \
  --service-name personalfood-service \
  --task-definition personalfood:1 \
  --desired-count 2 \
  --launch-type FARGATE \
  --load-balancers targetGroupArn=arn:aws:elasticloadbalancing:...,containerName=personalfood,containerPort=8080 \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-xxx],securityGroups=[sg-xxx],assignPublicIp=ENABLED}" \
  --region us-east-1
```

---

## ✅ Opção 3: CI/CD com GitHub Actions

Crie `.github/workflows/deploy.yml`:

```yaml
name: Deploy to AWS

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1
      
      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v1
      
      - name: Build, tag, and push image to Amazon ECR
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ github.sha }}
        run: |
          docker build -t $ECR_REGISTRY/personalfood:$IMAGE_TAG .
          docker push $ECR_REGISTRY/personalfood:$IMAGE_TAG
      
      - name: Update ECS Service
        run: |
          aws ecs update-service \
            --cluster personalfood-cluster \
            --service personalfood-service \
            --force-new-deployment \
            --region us-east-1
```

---

## 🔒 Segurança - Checklist

- [ ] Usar AWS Secrets Manager para senhas e tokens
- [ ] Ativar HTTPS com AWS Certificate Manager
- [ ] Configurar WAF (Web Application Firewall)
- [ ] Ativar VPC FlowLogs para monitoramento
- [ ] Usar IAM Roles (nunca keys hardcoded)
- [ ] Habilitar backups automáticos do RDS
- [ ] Configurar CloudWatch alarms
- [ ] Ativar logging de todas as operações

---

## 💰 Estimativa de Custos (US$ mensais)

| Serviço | Tier | Custo |
|---------|------|-------|
| EC2 t3.micro | Free Tier (750h/mês) | $0 |
| RDS PostgreSQL db.t3.micro | Free Tier (750h/mês) | $0 |
| Data Transfer | 1GB/mês | ~$0.50 |
| **Total (Free Tier)** | - | **~$0.50** |
| **Com ECS Fargate** | 512MB mem | ~$15-30/mês |

---

## 🆘 Troubleshooting

**Erro: Connection refused**
```bash
# Verificar se app está rodando
docker-compose ps
docker-compose logs app

# Verificar port binding
netstat -tlnp | grep 8080
```

**Erro: Database connection failed**
```bash
# Verificar conectividade PostgreSQL
docker-compose exec app \
  java -cp "$(find . -name "postgresql*.jar" -print0 | tr '\0' ':')" \
  org.postgresql.util.DriverRegistry

# Ou usar psql
psql -h {rds-endpoint} -U postgres -d personal_food
```

**Logs em tempo real**
```bash
docker-compose logs -f app
# ou
aws logs tail /ecs/personalfood --follow
```

---

## 📞 Suporte e Documentos Úteis

- AWS Docs: https://docs.aws.amazon.com
- Spring Boot AWS: https://aws.amazon.com/java/springboot
- PostgreSQL on AWS RDS: https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/PostgreSQL
- ECS Best Practices: https://aws.amazon.com/blogs/containers

---

**Última atualização**: Abril 2026
**Status**: Pronto para produção ✅
