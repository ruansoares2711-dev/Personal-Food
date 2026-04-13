🥗 Personal Food - Encontre o Chef Ideal

A Personal Food (PF) é uma plataforma web inovadora que conecta clientes a chefs profissionais especializados em alimentação personalizada. Funcionando como um "iFood de Chefs", o nosso objetivo é proporcionar uma experiência culinária sob medida no conforto de casa, combinando praticidade com um toque humano.

---

## 🚀 Quick Start

### Opção 1: Docker Compose (Recomendado)
```bash
docker-compose up -d
# Acesse: http://localhost:8080
```

### Opção 2: Maven Wrapper + Docker
```bash
# Terminal 1 - Inicie PostgreSQL
docker run --name personalfood-db -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:15-alpine

# Terminal 2 - Rode a aplicação
.\mvnw.cmd spring-boot:run
# Acesse: http://localhost:8080
```

### Opção 3: JAR Direto
```bash
java -jar .\target\PersonalFood-0.0.1-SNAPSHOT.jar
# Acesse: http://localhost:8080
```

---

## 📚 Documentação

- **[SETUP_FINAL.md](SETUP_FINAL.md)** - Resumo completo de todas as correções
- **[LOCAL_SETUP.md](LOCAL_SETUP.md)** - Guia de configuração local
- **[AWS_DEPLOYMENT.md](AWS_DEPLOYMENT.md)** - Deploy na AWS (EC2, ECS, RDS)
- **[TESTING.md](TESTING.md)** - Testes da API e endpoints

---

## ✅ Status

- ✅ Migração MySQL → PostgreSQL completa
- ✅ Hibernate configurado corretamente
- ✅ Lombok removido (getters/setters manuais)
- ✅ Build compilado com sucesso
- ✅ Docker Compose pronto
- ✅ Pronto para produção

---

## 🛠️ Stack Tecnológico

### Backend
- **Java 21** (Eclipse Temurin)
- **Spring Boot 3.2.4**
- **Spring Data JPA & Hibernate**
- **PostgreSQL 15**
- **Spring Security + OAuth2 Google**

### Frontend
- **HTML5** - Estruturação semântica
- **CSS3** - Flexbox, Grid, Dark Theme
- **Vanilla JavaScript** - Interatividade

### DevOps
- **Docker** - Containerização
- **Docker Compose** - Orquestração
- **AWS** - Hospedagem (EC2, RDS, ECS)

---

## 📖 Funcionalidades

Google Fonts: Tipografia moderna utilizando as fontes Poppins e Montserrat.

🎨 Design System e Padrões Visuais

Foi criada uma identidade visual coesa para toda a plataforma, garantindo uma navegação intuitiva:

Navbar Fixa: Barra de navegação superior padrão em todas as páginas, garantindo fácil acesso às secções principais.

Cores Principais:

Fundo (--bg-main): #000000 (Preto)

Navegação (--nav-bg): #113c0b (Verde Escuro)

Acentos (--btn-green): #a7c938 (Verde Claro)

Logo (--logo-bg): #d1c115 (Amarelo)

Chamadas para Ação (--btn-red): #cc0000 (Vermelho)

⚙️ Como Executar o Projeto

Como o projeto é constituído por ficheiros estáticos (HTML/CSS/JS), não é necessária a instalação de dependências ou servidores complexos para visualizar o layout.

Faça o clone do repositório:

git clone [https://github.com/ruansoares2711-dev/Personal-Food.git]


Navegue até à pasta do projeto:

cd personal-food


Abra o ficheiro index.html diretamente no seu navegador web preferido (Chrome, Firefox, Safari, Edge) ou utilize a extensão Live Server no VS Code para uma melhor experiência de desenvolvimento.

🔜 Próximos Passos (Roadmap)

[ ] Integração com Back-end (APIs para listagem dinâmica de chefs e receitas).

[ ] Implementação do sistema de Login / Registo de utilizadores.

[ ] Criação do painel de controlo (Dashboard) para os Chefs gerirem os seus pedidos.

[ ] Sistema de carrinho e checkout para contratação de serviços e cursos pagos.

Desenvolvido com 💚 pela equipa Personal Food.
