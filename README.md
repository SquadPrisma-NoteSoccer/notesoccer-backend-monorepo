# 🏟️ NoteSoccer - Backend Monorepo

Este repositório contém todos os **microserviços backend** do projeto **NoteSoccer**, organizado em um **monorepo** para facilitar a integração, versionamento e manutenção.

💡 Este backend segue uma arquitetura moderna com Java 21, Spring Boot, PostgreSQL (Supabase e padrões de integração entre micros.
<br><br>
## 📂 Estrutura

/orchestration-service     # Camada de orquestração (Sagas/Workflows)
/user-service              # Gestão de usuários
/league-service            # Gestão de Ligas e times
/match-service             # Gestão de Partidas
/flyer-service             # Geração de flyers
/notification-service      # Notificações
/docker                    # Arquivos docker-compose e configs de execução local
<br><br>
## 🚀 Tecnologias

- **Java 21** + **Spring Boot 3**
- **Maven** (multi-módulo)
- **PostgreSQL** (via Supabase)
- **Flyway** para migrations
- **Docker +  Docker Compose**
- **OpenFeign (comunicação entre serviços)**
- **GitHub Actions** para CI/CD
<br><br>
## 🐳 Executando o Backend Localmente (Docker Compose)

Todos os serviços podem ser executados com um único comando utilizando o docker-compose.yml localizado na pasta /docker.

1️⃣ Pré-requisitos
- Docker instalado
- Docker Compose instalado
- Credenciais do Supabase (fornecidas internamente)
- Java 21 (apenas se quiser rodar algum serviço fora do Docker)
<br><br>
## 🔐 Configurações e Secrets

- As credenciais NÃO ficam neste repositório e não são commitadas.
- Arquivos sensíveis (`application-dev.properties`, `application-prod.properties`) **não devem ser commitados**.
- Cada serviço possui um arquivo de exemplo `application-example.properties` que deve ser copiado e renomeado para o ambiente desejado:
  ```bash
  # Linux/Mac
  cp src/main/resources/application-example.properties src/main/resources/application-dev.properties
  
  # Windows PowerShell
  Copy-Item src/main/resources/application-example.properties src/main/resources/application-dev.properties
  ```

  Preencha as variáveis de ambiente necessárias (DB_URL, DB_USERNAME, DB_PASSWORD, etc.) de acordo com seu setup local.
<br><br>
## 📥 Como obter as credenciais

As credenciais (DB_URL, DB_PASSWORD, JWT_SECRET etc.) ficam armazenadas internamente no:
👉 Notion da Squad NoteSoccer

Para participar ou rodar o projeto localmente:
1. Solicite acesso ao Tech Lead
2. Copie as credenciais corretas
3. Preencha cada application-dev.properties conforme seu serviço

⚠️ Nada disso deve ser commitado.
<br><br>
## ▶️ Subir todos os serviços
No diretório /docker:

```bash
docker compose up --build
```

Esse comando irá:
- Subir todos os micros em containers isolados
- Aplicar migrations Flyway
- Conectar ao banco do Supabase
- Iniciar automaticamente as dependências internas

### Parar
```bash
docker compose down
```

<br><br>
## 🤝 Contribuindo com o projeto

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
   *refactor: Refatorações
   *test: Cobertura de testes
5. Abra um Pull Request para develop.
<br><br>
## 📌 Status Atual:
### Concluído
- Estrutura inicial do monorepo
- User Service + Auth + fluxo de cadastro/login
- Orchestration Service em bootstrap
- Integração Backend ↔ Frontend funcionando (cadastro, ligas, times)

### Em anndamento
- Partidas
- Flyer Service
- Notificações
- Regras do Matchmaking
<br><br><br>
## 🎉 Pronto para rodar o NoteSoccer?
Copie os arquivos example → configure seu ambiente → execute:
   ```bash
   docker compose up --build
   ```
E o backend estará rodando localmente.
