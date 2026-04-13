# 🚀 Guia Rápido - Local Development

## Como rodar localmente (pós-correção)

### Prerequisitos
- Java 21+
- Docker (para PostgreSQL)
- Maven 3.9+

### Passo 1: Inicie o PostgreSQL com Docker

```powershell
# Terminal PowerShell
docker run --name personalfood-db `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=password `
  -e POSTGRES_DB=personal_food `
  -p 5432:5432 `
  -d postgres:15-alpine
```

### Passo 2: Verifique se o .env está correto

```powershell
# Arquivo .env deve estar na raiz do projeto com:
cat .env
```

Conteúdo esperado:
```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/personal_food
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
MP_TOKEN=TEST-xxx
EMAIL_PASSWORD=xxx
GEMINI_API_KEY=xxx
GOOGLE_CLIENT_ID=xxx
GOOGLE_CLIENT_SECRET=xxx
```

### Passo 3: Rodar a aplicação

**Opção A - Com Maven Wrapper:**
```powershell
cd c:\Users\Administrator\Github\PersonalFood
.\mvnw.cmd spring-boot:run
```

**Opção B - Build JAR e rodar:**
```powershell
.\mvnw.cmd clean package -DskipTests
java -jar .\target\PersonalFood-0.0.1-SNAPSHOT.jar
```

**Opção C - Com Docker Compose (tudo junto):**
```powershell
docker-compose up -d
docker-compose logs -f app
```

### Passo 4: Acessar a aplicação

```
http://localhost:8080
```

### Parar a aplicação

```powershell
# Se rodou com Docker Compose:
docker-compose down

# Se rodou com Maven, Ctrl+C no terminal

# Se rodou com Java diretamente, Ctrl+C no terminal
```

---

## 📊 Problemas comuns e soluções

| Problema | Solução |
|----------|---------|
| Port 5432 já está em uso | `docker stop personalfood-db` |
| JDBC connection error | Verificar `.env` e se PostgreSQL está rodando |
| Build falha por Lombok | Rodar `.\mvnw.cmd clean install` |
| Application.properties não carrega | Checar se arquivo existe em `src/main/resources/` |

---

## 🎯 Próximos passos

1. **Rodar localmente** → Seguir guia acima
2. **Testar endpoints** → Usar Postman/Insomnia
3. **Deploy AWS** → Ver `AWS_DEPLOYMENT.md`
4. **CI/CD** → Configurar GitHub Actions

---

**Status**: ✅ Pronto para desenvolvimento
