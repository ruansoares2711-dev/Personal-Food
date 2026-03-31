🥗 Personal Food - Encontre o Chef Ideal

A Personal Food (PF) é uma plataforma web inovadora que conecta clientes a chefs profissionais especializados em alimentação personalizada. Funcionando como um "iFood de Chefs", o nosso objetivo é proporcionar uma experiência culinária sob medida no conforto de casa, combinando praticidade com um toque humano.

Seja para atletas com metas nutricionais, famílias com restrições alimentares ou pessoas que procuram reeducação alimentar sem ter de cozinhar: Nós temos o chef certo para si.

🚀 Funcionalidades da Plataforma

A plataforma está dividida em várias secções desenhadas para oferecer uma experiência completa de gastronomia e aprendizagem:

👨‍🍳 Chef's: Catálogo de chefs profissionais categorizados por especialidade (Eventos, Particular, Vegano, Corporativo). Permite visualizar perfis, avaliações e solicitar serviços.

📚 Cursos: Aulas de culinária para diversos níveis (Iniciante, Intermédio, Avançado) abrangendo várias categorias como doces, comidas saudáveis e nacionalidades.

📖 Receitas: Uma área dedicada a "Cozinha sem mistérios", com receitas categorizadas por dificuldade e tempo de preparação.

🤝 PF Impacta: A nossa missão de responsabilidade social. Projetos de formação, inclusão e geração de conhecimento através da gastronomia em comunidades (ex: Paraisópolis, Heliópolis, Jardim Peri).

ℹ️ Sobre Nós: Informações sobre a visão da empresa e os benefícios da plataforma.

🛠️ Tecnologias Utilizadas

O front-end foi desenvolvido utilizando tecnologias web padrão, com foco em performance e responsividade:

HTML5: Estruturação semântica de todas as páginas.

CSS3: Estilização com Flexbox, CSS Grid e variáveis globais (:root) para um design system consistente (Dark Theme).

JavaScript (Vanilla): Lógica de interface (filtros de categorias, modais customizados, interatividade nos menus).

Google Fonts: Tipografia moderna utilizando as fontes Poppins e Montserrat.

📁 Estrutura de Ficheiros

O projeto está organizado da seguinte forma para facilitar a manutenção e escalabilidade:

personal-food/
│
├── index.html                 # Página inicial (Home)
│
├── sections/                  # Páginas internas da plataforma
│   ├── chefs.html             # Catálogo de Chefs
│   ├── cursos.html            # Cursos de culinária
│   ├── receitas.html          # Lista de receitas
│   ├── impacta.html           # Ações sociais (PF Impacta)
│   └── sobre.html             # Página Sobre Nós
│
├── assets/                    # Recursos estáticos (Imagens, Ícones, etc.)
│   ├── personalFood/          # Logos e identidade visual
│   │   ├── PersonalFoodIcon.png
│   │   └── PersonalFoodLogo.png
│   └── tomatito/              # Ícones do mascote e categorias
│
└── README.md                  # Documentação do projeto


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

git clone [https://github.com/seu-usuario/personal-food.git](https://github.com/seu-usuario/personal-food.git)


Navegue até à pasta do projeto:

cd personal-food


Abra o ficheiro index.html diretamente no seu navegador web preferido (Chrome, Firefox, Safari, Edge) ou utilize a extensão Live Server no VS Code para uma melhor experiência de desenvolvimento.

🔜 Próximos Passos (Roadmap)

[ ] Integração com Back-end (APIs para listagem dinâmica de chefs e receitas).

[ ] Implementação do sistema de Login / Registo de utilizadores.

[ ] Criação do painel de controlo (Dashboard) para os Chefs gerirem os seus pedidos.

[ ] Sistema de carrinho e checkout para contratação de serviços e cursos pagos.

Desenvolvido com 💚 pela equipa Personal Food.
