# Personal Food - Contexto Geral do Projeto

## 📋 Visão Geral

**Personal Food (PF)** é uma plataforma web que conecta **clientes** a **chefs profissionais** especializados em alimentação personalizada. Funciona como um "iFood de Chefs", permitindo que os clientes contratem chefs para eventos, refeições personalizadas e cursos culinários.

---

## 🏗️ Arquitetura do Projeto

### Backend
- **Framework**: Spring Boot 3.x
- **Banco de Dados**: PostgreSQL 15+
- **ORM**: Hibernate/JPA
- **Autenticação**: Spring Security + OAuth2 (Google)
- **API**: REST (JSON)
- **Segurança**: CSRF Token, SessionManagement, HTTPS (SSL/TLS)

### Frontend
- **Linguagem**: HTML5, CSS3, JavaScript Vanilla
- **Estrutura**: SPA (Single Page Application) com carregamento dinâmico
- **Armazenamento Local**: LocalStorage para dados de usuário e conteúdo personalizado
- **Design**: Dark theme customizado

### Deployment
- **Containerização**: Docker + Docker Compose
- **Cloud**: AWS (EC2, ECS, RDS)
- **CI/CD**: Scripts PowerShell/Bash para deploy automatizado

---

## 👥 Atores do Sistema

1. **CLIENTE**: Busca contratar chefs, compra cursos, realiza pagamentos (PIX/Cartão)
2. **CHEFE**: Oferece serviços, gerencia pedidos, envia orçamentos
3. **ADMIN**: Aprova novos chefs, gerencia conteúdo (cursos, receitas, impacta)

---

## 📊 Modelo de Dados

### Tabelas Principais
- `usuarios` - CPF, email, senha (bcrypt), tipo, foto
- `chefes` - Especialidade, apresentação, foto perfil, reputação, vitrine
- `pedidos_evento` - Cliente, chef, data, orçamento, status
- `logs_aprovacao` - Histórico de aprovação de chefs
- `password_reset_tokens` - Recovery de senha

---

## 🔒 Configuração de Segurança (SecurityConfig.java)

### Rotas Públicas
- `/` (homepage)
- `/index.html`
- `/sobre.html`
- `/assets/**` (imagens, CSS, JS)
- `/api/usuarios/login`
- `/api/usuarios/registrar`
- `/api/usuarios/recuperar-senha`
- `/api/usuarios/confirmar-nova-senha`
- `/sections/reset-password.html`

### Rotas Protegidas
- `/api/pagamentos/**` - Requer CLIENTE, CHEFE ou ADMIN
- `/api/admin/**` - Requer ADMIN
- `/sections/**` - Requer autenticação (exceções acima)

### Configurações CSRF
- CSRF habilitado globalmente
- Exceção: `/api/pagamentos/**` (desabilitado)
- Cookie CSRF com `HttpOnly=false` para acesso JS

---

## 💾 Persistência de Conteúdo

### Dados de Admin (Novo Sistema)
O conteúdo de admin é **salvo em LocalStorage** (navegador) com timestamp:
- `cursos-personalizados` - Novos cursos adicionados
- `impacta-eventos` - Novos eventos sociais
- `receitas-personalizadas` - Novas receitas

**Estrutura**:
```json
{
  "titulo": "Nome",
  "chef": "Chef Name",
  "preco": "R$ 100",
  "url": "imagem.jpg",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

### Alternativa com Banco de Dados
Para persistência permanente, criar:
- Tabela `conteudo_customizado` (id, tipo, dados_json, criado_por, data_criacao)
- Endpoint: `POST /api/admin/conteudo` (salvar em BD)

---

## 🔄 Fluxos Principais

### 1. Registro de Usuário
1. Cliente/Chef preenche formulário em `registerLogin.html`
2. Envia POST para `/api/usuarios/registrar`
3. Se CHEFE → cria registro em `chefes` com status PENDENTE_CHEFE
4. Admin aprova em `painel-admin.html`

### 2. Contratação de Chef
1. Cliente busca chef por especialidade
2. Clica em "Solicitar Orçamento"
3. Preenche detalhes do evento
4. Chef recebe notificação em `painel-chefe.html`
5. Chef envia orçamento
6. Cliente aprova ou rejeita
7. Se aprovado → redireciona para pagamento

### 3. Gerenciamento de Conteúdo (Admin)
1. Admin logged faz login
2. Navega para `cursos.html`, `impacta.html` ou `receitas.html`
3. Clica no botão ⚙️ (admin)
4. Modal abre com formulário
5. Preenche dados e clica "Adicionar"
6. Conteúdo é salvo em LocalStorage
7. Página recarrega e mostra novo item

---

## 🗂️ Estrutura de Pastas

```
src/main/
├── java/com/pf/PersonalFood/
│   ├── Application.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── UsuarioController.java
│   │   ├── ChefeController.java
│   │   ├── AdminController.java
│   │   └── PagamentoController.java
│   ├── service/
│   │   ├── EmailService.java
│   │   ├── CustomOAuth2UserService.java
│   │   └── OrcamentoService.java
│   ├── model/
│   │   ├── Usuario.java
│   │   ├── Chefe.java
│   │   ├── PedidoEvento.java
│   │   ├── LogAprovacao.java
│   │   └── TipoUsuario.java (ENUM)
│   ├── repository/
│   │   ├── UsuarioRepository.java
│   │   ├── ChefeRepository.java
│   │   ├── PedidoEventoRepository.java
│   │   └── LogAprovacaoRepository.java
│   └── dto/
│       ├── LoginDTO.java
│       ├── RegistroDTO.java
│       └── UsuarioResponseDTO.java
├── resources/
│   ├── application.properties
│   ├── application-dev.properties
│   ├── application-aws.properties
│   └── static/
│       ├── index.html
│       ├── assets/
│       └── sections/
│           ├── registerLogin.html
│           ├── painel-admin.html
│           ├── painel-chefe.html
│           ├── painel-cliente.html
│           ├── cursos.html (com admin)
│           ├── impacta.html (com admin)
│           ├── receitas.html (com admin)
│           └── ...outros
```

---

## 🔑 Endpoints Principais

### Autenticação
- `POST /api/usuarios/login` - Login
- `POST /api/usuarios/registrar` - Registro
- `POST /api/usuarios/recuperar-senha` - Reset de senha
- `PUT /api/usuarios/confirmar-nova-senha` - Confirmar nova senha

### Chefs
- `GET /api/chefes` - Listar chefs
- `GET /api/chefes/{id}` - Detalhes do chef
- `PUT /api/chefes/perfil/{usuarioId}` - Atualizar perfil
- `GET /api/chefes/pedidos/{usuarioId}` - Pedidos do chef

### Admin
- `PUT /api/admin/aprovar-chefe/{id}` - Aprovar chef
- `POST /api/admin/conteudo` - Salvar conteúdo customizado (futuro)

### Pagamentos
- `POST /api/pagamentos/pix` - Gerar PIX
- `POST /api/pagamentos/cartao` - Processar cartão (Mercado Pago)

---

## 🛠️ Tecnologias Utilizadas

| Aspecto | Tecnologia |
|--------|-----------|
| Backend | Spring Boot 3.x, Spring Security, Spring Data JPA |
| Banco de Dados | PostgreSQL 15+ |
| Frontend | HTML5, CSS3, JavaScript (Vanilla) |
| Autenticação | OAuth2 (Google), JWT (custom tokens) |
| Email | Spring Mail + SMTP |
| Pagamentos | Mercado Pago API |
| Cloud | AWS (EC2, RDS, ECS) |
| Containerização | Docker, Docker Compose |
| Build | Maven |

---

## 💡 Prompt para Redução de Tokens (Claude)

### Para próximas interações, use este prompt:

```
Você é um assistente especializado em Personal Food.

CONTEXTO RESUMIDO:
- Plataforma: Spring Boot 3.x + PostgreSQL + React/Vanilla JS
- Atores: Cliente, Chef, Admin
- Fluxo: Clientes contratam chefs para eventos
- Segurança: OAuth2 + JWT + CSRF

RESTRIÇÕES DE TOKEN:
1. Resuma explicações em 1-2 parágrafos
2. Use tabelas em vez de listas longas
3. Mostre apenas código relevante (máx 30 linhas)
4. Omita imports/boilerplate padrão
5. Para mudanças múltiplas, crie um checklist

PREFIXO PARA MUDANÇAS:
- [BACKEND] para código Java
- [FRONTEND] para HTML/CSS/JS
- [INFRA] para Docker/Deploy
- [DB] para migrations/SQL

EVITE:
- Explicações sobre conceitos básicos (Spring, REST, etc)
- Exemplos não-relevantes
- Duplicação de código
```

---

## 📝 Notas para Desenvolvimento

### Melhorias Futuras
1. Persistência de conteúdo admin no banco (não só localStorage)
2. Notificações em tempo real (WebSocket)
3. Sistema de avaliações de chefs
4. Integração com Stripe (além de Mercado Pago)
5. PWA (Progressive Web App)

### Problemas Conhecidos
- LocalStorage tem limite de ~5MB por domínio
- Imagens grandes precisam de otimização
- CSRF pode causar problemas em APIs externas

### Performance
- Minificar CSS/JS em produção
- Lazy load de imagens
- Cache HTTP com CDN
- Pagination para listas grandes

---

**Última Atualização**: Janeiro 2025  
**Versão**: 1.0 (Post-Migration PostgreSQL)
