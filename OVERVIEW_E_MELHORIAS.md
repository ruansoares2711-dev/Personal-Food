# 📊 Overview - Personal Food

## 🎯 O que é?

Personal Food é um marketplace de chefs que conecta:
- **CLIENTE**: Contrata chefs para eventos ou refeições
- **CHEF**: Oferece serviços, responde orçamentos
- **ADMIN**: Aprova chefes, gerencia conteúdo

**Fluxo Core**: Cliente → Busca Chef → Solicita Orçamento → Chef Propõe → Cliente Aprova → Pagamento → Evento

---

## ✅ O que já funciona

- ✅ Autenticação (Login/Registro)
- ✅ Recuperação de Senha
- ✅ CRUD de Perfis (Cliente/Chef)
- ✅ Sistema de Orçamento (Kanban Pipeline)
- ✅ Pagamentos (PIX/Cartão via Mercado Pago)
- ✅ Painel Admin (Aprovação de Chefes)
- ✅ Gerenciamento de Conteúdo (Cursos, Receitas, Impacta)
- ✅ Chatbot (Gemini Integration)
- ✅ Deploy (Docker + AWS)

---

## 🔴 PROBLEMAS CRÍTICOS (Regra de Negócio)

### 1. **Email NÃO é validado/único no registro**
**Problema**: Dois usuários podem se registrar com o mesmo email  
**Impacto**: Login quebrado, confusão de dados  
**Solução**:
```java
@Column(unique = true)
private String email;  // Já tem, mas falta validação

// No Controller:
Optional<Usuario> existe = usuarioRepo.findByEmail(dto.getEmail());
if (existe.isPresent()) {
    return ResponseEntity.badRequest().body("Email já cadastrado!");
}
```

### 2. **CPF não é validado (formato/existência)**
**Problema**: CPF inválido ou duplicado é aceito  
**Impacto**: Dados inconsistentes  
**Solução**:
```java
// Usar biblioteca: br.com.caelum.stella
if (!CPFValidator.isValid(cpf)) {
    return ResponseEntity.badRequest().body("CPF inválido!");
}

@Column(unique = true)
private String cpf;
```

### 3. **Senha fraca sem validação**
**Problema**: Usuário cria `123456` como senha  
**Solução**:
```java
// Adicionar validação no DTO
@Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=]).{8,}$",
         message = "Mínimo 8 chars, 1 maiúscula, 1 número, 1 especial")
private String senha;
```

### 4. **Admin pode remover usuários sem auditoria real**
**Problema**: DELETE direto sem logs adequados  
**Solução**: Usar soft delete (adicionar `deletedAt` timestamp)
```java
@Column(name = "deletado_em")
private LocalDateTime deletadoEm;

// Queries devem filtrar: WHERE deletado_em IS NULL
```

### 5. **Status do Pedido está com valores hardcoded**
**Problema**: `NOVA_SOLICITACAO`, `ORCAMENTO_ENVIADO`, `CONFIRMADO`, `RECUSADO` espalhados pelo código  
**Solução**: Criar ENUM
```java
public enum StatusPedido {
    NOVA_SOLICITACAO,
    ORCAMENTO_ENVIADO,
    AGUARDANDO_PAGAMENTO,
    CONFIRMADO,
    RECUSADO,
    CANCELADO
}
```

### 6. **Conteúdo Admin (Cursos/Receitas) está em localStorage**
**Problema**: Dados não persistem, perdem ao trocar navegador  
**Solução**: Criar tabela `conteudo_customizado` no BD
```java
@Entity
@Table(name = "conteudo_customizado")
public class ConteudoCustomizado {
    private Integer id;
    private String tipo; // CURSO, RECEITA, IMPACTA
    private String titulo;
    private String descricao;
    private String urlImagem;
    private Integer criadoPor; // FK usuarioId (ADMIN)
    private LocalDateTime dataCriacao;
}
```

### 7. **Valor do Orçamento sem limite/validação**
**Problema**: Chef pode colocar `R$ 999999999999`  
**Solução**:
```java
@DecimalMin("1.00")
@DecimalMax("99999.99")
private BigDecimal valorOrcamento;
```

### 8. **Sem verificação de conflito de agenda**
**Problema**: Chef pode ser contratado 2x no mesmo dia  
**Solução**: Consulta antes de criar pedido
```java
List<PedidoEvento> pedidosDoChef = pedidoRepo.findByChefIdAndDataEvento(
    chefeId, dataEvento, "CONFIRMADO"
);
if (!pedidosDoChef.isEmpty()) {
    return ResponseEntity.badRequest().body("Chef já possui evento nesta data!");
}
```

### 9. **Token de Reset de Senha sem rate limiting**
**Problema**: Usuário pode gerar 1000 tokens em segundos  
**Solução**: Cache com rate limit
```java
// Usar Spring's RateLimiter ou Bucket4j
@CacheEvict(value = "passwordReset", key = "#email", condition = "...")
```

### 10. **Foto de Perfil sem validação de tamanho**
**Problema**: Upload de imagem gigante quebra o servidor  
**Solução**:
```java
@Column(columnDefinition = "TEXT") // Limite a ~100KB em BASE64
private String fotoPerfil;

// No Controller:
if (fotoPerfil.length() > 500_000) { // ~375KB
    return ResponseEntity.badRequest().body("Imagem muito grande!");
}
```

---

## 🟡 PROBLEMAS FUNCIONAIS (UX/Performance)

### 11. **Página carrega todos os chefs (sem paginação)**
**Impacto**: Lento com 10.000 chefs  
**Solução**:
```java
@GetMapping
public ResponseEntity<Page<Chefe>> listarChefes(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam String especialidade
) {
    return ResponseEntity.ok(
        chefeRepo.findByEspecialidadeContaining(especialidade, PageRequest.of(page, size))
    );
}
```

### 12. **Sem busca/filtro de chefs por especialidade**
**Problema**: Cliente não consegue filtrar (Ex: "Chefs de Sushi")  
**Solução**: Front já tem, falta endpoint otimizado
```java
// Já existe, mas sem pagination:
chefeRepo.findByAtivoVitrineTrueAndEspecialidadeContainingIgnoreCase(especialidade)
```

### 13. **Sem notificações em tempo real (WebSocket)**
**Problema**: Chef vê pedido novo só recarregando página  
**Solução**: Implementar WebSocket
```java
// spring-boot-starter-websocket
@Configuration
@EnableWebSocket
public class WebSocketConfig { ... }
```

### 14. **Sem histórico de alterações de pedido**
**Problema**: Não dá pra auditar "quando mudou de status"  
**Solução**: Criar `PedidoHistorico`
```java
@Entity
public class PedidoHistorico {
    private Integer id;
    private Integer pedidoId;
    private String statusAnterior;
    private String statusNovo;
    private LocalDateTime dataAlteracao;
}
```

### 15. **Avaliação de Chef não existe**
**Problema**: Cliente não consegue avaliar chef após evento  
**Solução**: CRUD de avaliações
```java
@Entity
public class Avaliacao {
    private Integer id;
    private Integer chefeId;
    private Integer clienteId;
    private Integer nota; // 1-5
    private String comentario;
    private LocalDateTime data;
}
```

### 16. **Mercado Pago com hardcoded URLs**
**Problema**: `http://localhost:8080` e `https://personalfood.com.br` misturados no código  
**Solução**: Usar variável de environment
```properties
# application.properties
app.base-url=${BASE_URL:http://localhost:8080}
```

### 17. **Sem tratamento de timeout em Gemini**
**Problema**: Se Gemini cair, chatbot fica pendurado  
**Solução**:
```java
@GetMapping("/pergunta")
public String perguntar(@RequestParam String mensagem) {
    try {
        return client.models.generateContent("gemini-2.5-flash", mensagem, null);
    } catch (Exception e) {
        // Log e retorna resposta padrão
        return "Desculpe, estou com dificuldade. Tente novamente.";
    }
}
```

### 18. **Sem cache de dados públicos (chefs, receitas)**
**Problema**: Cada request consulta BD  
**Solução**: Cache Redis
```java
@Cacheable(value = "chefs", key = "'all'")
public List<Chefe> listarChefes() { ... }
```

### 19. **CORS permite tudo (`*`)**
**Problema**: Security risk  
**Solução**:
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("https://personalfood.com.br")
                    .allowedMethods("GET", "POST", "PUT")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
```

### 20. **Sem rate limiting em login (força bruta)**
**Problema**: Alguém pode tentar 10.000 senhas/segundo  
**Solução**:
```java
@Component
public class LoginRateLimiter {
    private RateLimiter limiter = RateLimiter.create(5.0); // 5 tentativas/seg por IP
    
    public boolean isAllowed(String email) {
        return limiter.tryAcquire();
    }
}
```

---

## 📋 MATRIZ DE PRIORIDADE

| ID  | Problema | Severidade | Impacto | Esforço | Prioridade |
|-----|----------|-----------|--------|--------|-----------|
| 1   | Email não validado | 🔴 Alto | Crítico | Baixo | P0 |
| 2   | CPF não validado | 🔴 Alto | Crítico | Baixo | P0 |
| 3   | Senha fraca | 🟡 Médio | Segurança | Baixo | P1 |
| 4   | Soft Delete | 🟡 Médio | Auditoria | Médio | P1 |
| 5   | Status ENUM | 🟡 Médio | Manutenção | Baixo | P1 |
| 6   | Conteúdo em BD | 🟡 Médio | Persistência | Médio | P1 |
| 7   | Limite Orçamento | 🟡 Médio | Negócio | Baixo | P1 |
| 8   | Conflito Agenda | 🔴 Alto | Negócio | Médio | P0 |
| 9   | Rate Limit Senha | 🟡 Médio | Segurança | Baixo | P1 |
| 10  | Validação Imagem | 🟡 Médio | Performance | Baixo | P1 |
| 11  | Paginação Chefs | 🟡 Médio | Performance | Médio | P2 |
| 12  | Filtro Especialidade | 🟡 Médio | UX | Médio | P2 |
| 13  | WebSocket Notif | 🟡 Médio | UX | Alto | P2 |
| 14  | Histórico Pedido | 🟡 Médio | Auditoria | Médio | P2 |
| 15  | Avaliações | 🟢 Baixo | Negócio | Médio | P3 |
| 16  | Hardcoded URLs | 🟡 Médio | DevOps | Baixo | P1 |
| 17  | Timeout Gemini | 🟡 Médio | Confiabilidade | Baixo | P1 |
| 18  | Cache | 🟢 Baixo | Performance | Alto | P3 |
| 19  | CORS | 🔴 Alto | Segurança | Baixo | P0 |
| 20  | Rate Limit Login | 🔴 Alto | Segurança | Médio | P0 |

---

## 🚀 ROADMAP RECOMENDADO

### Fase 1 - Crítica (1-2 sprints)
- [ ] Email/CPF validação
- [ ] Conflito agenda
- [ ] CORS restrito
- [ ] Rate limit login
- [ ] Soft delete

### Fase 2 - Segurança (1 sprint)
- [ ] Validação senha forte
- [ ] Rate limit reset senha
- [ ] Validação tamanho imagem
- [ ] Hardcoded URLs

### Fase 3 - Negócio (2-3 sprints)
- [ ] Status ENUM
- [ ] Conteúdo em BD
- [ ] Limite orçamento
- [ ] Histórico pedido

### Fase 4 - Experiência (3-4 sprints)
- [ ] Paginação/Filtros
- [ ] WebSocket notificações
- [ ] Sistema de avaliações
- [ ] Cache

---

## 📌 Notas Importantes

1. **Testes**: 0% cobertura - adicionar unit tests nas correções
2. **Logs**: Usar SLF4J em todos os controllers
3. **Documentação**: Atualizar Swagger/OpenAPI
4. **CI/CD**: Adicionar testes na pipeline
5. **Monitoramento**: New Relic/Datadog para produção

