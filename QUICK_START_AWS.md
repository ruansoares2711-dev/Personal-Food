# ⚡ Quick Start - Deployment AWS

## Seu Endpoint RDS
```
personalfood-db.c5eucqgwa20t.us-east-2.rds.amazonaws.com
```

## 3 Passos para Deploy

### 1️⃣ Build e Push no ECR (execute no seu terminal)

```powershell
# Obter seu AWS Account ID
$AWS_ACCOUNT_ID = aws sts get-caller-identity --query Account --output text

# Executar script
.\deploy-aws.ps1 -AwsAccountId $AWS_ACCOUNT_ID -AwsRegion us-east-2
```

**Resultado esperado:**
- ✅ Login no ECR bem-sucedido
- ✅ Imagem compilada
- ✅ Imagem enviada ao ECR
- ✅ URI da imagem será exibida

### 2️⃣ Criar Task Definition no ECS

1. Acesse: https://console.aws.amazon.com/ecs
2. Clique: **Task Definitions** → **Create new task definition**
3. Family name: `personalfood-task`
4. Launch type: **Fargate**
5. Add container:
   - Name: `personalfood`
   - Image: `[ECR_URI_DO_PASSO_1]`
   - Port: `8080`
   - Environment variables (copie e cole):

```
DB_HOST=personalfood-db.c5eucqgwa20t.us-east-2.rds.amazonaws.com
DB_PORT=3306
DB_NAME=personal_food
DB_USER=admin
DB_PASSWORD=SUA_SENHA_RDS
MP_TOKEN=seu_token
EMAIL_PASSWORD=sua_senha
GEMINI_API_KEY=sua_chave
GOOGLE_CLIENT_ID=seu_id
GOOGLE_CLIENT_SECRET=seu_secret
```

6. Clique: **Create**

### 3️⃣ Criar Service no ECS

1. Clique: **Clusters** → **personalfood-cluster** (crie se não existir)
2. Clique: **Create service**
3. Configure:
   - Launch type: **Fargate**
   - Task Definition: `personalfood-task`
   - Service name: `personalfood-service`
   - Desired tasks: `1`
4. **Load Balancer** (Application Load Balancer)
   - Selecione: **Create new load balancer**
   - Name: `personalfood-alb`
   - Container port: `8080`
5. Clique: **Create service**

## ✅ Acessar sua aplicação

Após criar o service (5-10 minutos):

```powershell
aws elbv2 describe-load-balancers --region us-east-2 --query 'LoadBalancers[0].DNSName' --output text
```

Cole o resultado no navegador: `http://[DNS_NAME]`

---

## 📝 Arquivos criados/atualizados

✅ `Dockerfile` - Multi-stage build otimizado
✅ `.dockerignore` - Arquivos a ignorar no build
✅ `application-aws.properties` - Configuração para AWS RDS
✅ `deploy-aws.ps1` - Script PowerShell para build e push
✅ `deploy-aws.sh` - Script Bash para build e push
✅ `AWS_DEPLOYMENT_GUIDE.md` - Guia completo detalhado

---

## 🎯 Próximos passos

1. ✅ Garanta que sua `.env` está com UTF-8 sem BOM
2. ✅ Compile o projeto localmente: `.\mvnw.cmd clean package -DskipTests`
3. ✅ Execute o script de deploy
4. ✅ Crie a Task Definition
5. ✅ Crie o Service
6. ✅ Acesse a URL do Load Balancer

---

## 💡 Dicas

- **Teste localmente primeiro**: `docker build -t personalfood .`
- **Veja os logs**: `aws logs tail /ecs/personalfood-task --follow`
- **Monitore custos**: AWS tem free tier por 12 meses
- **Faça backup**: Configure snapshots automáticos do RDS

Qualquer dúvida, veja o `AWS_DEPLOYMENT_GUIDE.md`! 🚀
