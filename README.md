# 🏟️ NoteSoccer - Backend Monorepo

Este repositório contém todos os **microserviços backend** do projeto **NoteSoccer**, organizado em um **monorepo** para facilitar a integração, versionamento e manutenção.

## 📂 Estrutura

/orchestration-service # Orquestração de sagas e workflows
/user-service # Gestão de usuários
/auth-service # Autenticação e controle de usuários
/league-service # Criação de Ligas (todo)
/team-service # Gestão de times
/match-service # Gestão de partidas
/flyer-service # Criação de flyers de jogos
/notification-service # Envio de notificações
/shared-libs # Bibliotecas compartilhadas entre serviços

## 🚀 Tecnologias

- **Java 21** + **Spring Boot**
- **Maven** (multi-módulo)
- **PostgreSQL** (via Supabase)
- **Flyway** para migrations
- **Docker** (futuro)
- **GitHub Actions** para CI/CD

## 🔐 Configurações e Secrets

- Arquivos sensíveis (`application-dev.properties`, `application-prod.properties`) **não devem ser commitados**.
- Cada serviço possui um arquivo de exemplo `application-example.properties` que deve ser copiado e renomeado para o ambiente desejado:
  ```bash
  # Linux/Mac
  cp src/main/resources/application-example.properties src/main/resources/application-dev.properties
  
  # Windows PowerShell
  Copy-Item src/main/resources/application-example.properties src/main/resources/application-dev.properties
  ```

  Preencha as variáveis de ambiente necessárias (DB_URL, DB_USERNAME, DB_PASSWORD, etc.) de acordo com seu setup local.

## 🌱 Contribuindo

1. Clone o repositório:
  ```bash
    git clone https://github.com/SquadPrisma-NoteSoccer/notesoccer-backend-monorepo.git
  ```
2. Crie sua branch de feature:
   ```bash
   git checkout -b feature/nome-da-sua-feature
   ```
3. Faça commits seguindo o padrão Conventional Commits:
   *feat: nova funcionalidade
   *fix: correção de bug
   *chore: manutenção (build, .gitignore, dependências)
   *docs: documentação
4. Abra um Pull Request para main.

📌 Status:
- Estrutura inicial do monorepo criada ✅
- Orchestration Service em bootstrap 🚧
- Demais serviços aguardando implementação 🚀
