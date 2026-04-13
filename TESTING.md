# 🧪 TESTE RÁPIDO DA API - PersonalFood

## Após rodar a aplicação, teste estes endpoints:

### 1️⃣ Health Check (verificar se app está rodando)

```bash
curl http://localhost:8080/actuator/health
```

**Resposta esperada:**
```json
{
  "status": "UP"
}
```

### 2️⃣ Testar Banco de Dados

```bash
# GET - Listar todos os usuários
curl http://localhost:8080/api/usuarios

# GET - Listar todos os chefes
curl http://localhost:8080/api/chefes

# GET - Listar pedidos de eventos
curl http://localhost:8080/api/pedidos-evento
```

### 3️⃣ Criar um Novo Usuário (POST)

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@example.com",
    "senha": "senha123",
    "cpf": "12345678900",
    "tipo": "CLIENTE"
  }'
```

### 4️⃣ Testar Google OAuth2

1. Acesse http://localhost:8080
2. Clique em "Login com Google"
3. Você será redirecionado para o Google
4. Após login, a foto de perfil será salva automaticamente

### 5️⃣ Consultar Logs da Aplicação

```powershell
# Se usando Docker Compose:
docker-compose logs -f app

# Se usando Maven Wrapper:
# (Os logs aparecem direto no terminal)

# Se usando JAR:
java -jar PersonalFood-0.0.1-SNAPSHOT.jar
```

---

## ✅ Checklist de Testes

- [ ] Aplicação inicia sem erros
- [ ] Health check retorna UP
- [ ] Banco de dados está conectado
- [ ] Consegue listar usuários
- [ ] Consegue criar novo usuário
- [ ] Google OAuth funciona
- [ ] Foto de perfil é salva corretamente

---

## 📊 Endpoints Principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/usuarios` | Listar todos usuários |
| POST | `/api/usuarios` | Criar novo usuário |
| GET | `/api/usuarios/{id}` | Buscar usuário por ID |
| PUT | `/api/usuarios/{id}` | Atualizar usuário |
| DELETE | `/api/usuarios/{id}` | Deletar usuário |
| GET | `/api/chefes` | Listar todos chefes |
| GET | `/api/pedidos-evento` | Listar pedidos de evento |
| POST | `/api/pedidos-evento` | Criar novo pedido |
| GET | `/oauth2/authorization/google` | Login Google |

---

## 🔧 Ferramentas Recomendadas para Testar

1. **Postman** (GUI)
   - Download: https://www.postman.com/downloads/
   - Importe os endpoints e salve como coleção

2. **Insomnia** (GUI)
   - Download: https://insomnia.rest/download

3. **curl** (Linha de comando)
   - Já vem instalado no Windows 10+
   - Use os exemplos acima

4. **VS Code REST Client Extension**
   - Extension: `REST Client`
   - Crie arquivo `test.http` com os endpoints

---

## 📝 Exemplo: Arquivo test.http para VS Code

```http
### Health Check
GET http://localhost:8080/actuator/health

### Listar Usuários
GET http://localhost:8080/api/usuarios

### Criar Usuário
POST http://localhost:8080/api/usuarios
Content-Type: application/json

{
  "nome": "Maria Santos",
  "email": "maria@example.com",
  "senha": "senha123",
  "cpf": "98765432100",
  "tipo": "CLIENTE"
}

### Listar Chefes
GET http://localhost:8080/api/chefes

### Listar Pedidos
GET http://localhost:8080/api/pedidos-evento
```

Salve como `test.http` e clique no "Send Request" acima de cada endpoint!

---

## 🐛 Troubleshooting

### Erro: Connection refused
**Solução:** PostgreSQL não está rodando
```powershell
docker ps
docker start personalfood-db
```

### Erro: 404 Not Found
**Solução:** Endpoint não existe ou aplicação não iniciou
```powershell
# Verificar logs
docker-compose logs app
```

### Erro: 500 Internal Server Error
**Solução:** Erro na aplicação
```powershell
# Ver logs completos
docker-compose logs -f app | grep -i error
```

---

**Pronto para testar! 🚀**
