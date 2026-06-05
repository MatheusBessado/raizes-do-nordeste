# 🌽 Raízes do Nordeste — API Backend

> **Projeto Acadêmico Multidisciplinar — Trilha de Engenharia de Software / TI — UNINTER 2026**  
> Sistema de gestão de pedidos e controle de estoque para a rede de lanchonetes regional **Raízes do Nordeste**.

---

## 📋 Sumário

1. [Descrição e Arquitetura](#-descrição-e-arquitetura)
2. [Tecnologias Utilizadas](#-tecnologias-utilizadas)
3. [Como Executar o Projeto](#-como-executar-o-projeto)
   - [Execução via Docker (Recomendado)](#1-execução-via-docker-recomendado)
   - [Execução Local Manual](#2-execução-local-manual)
4. [Usuários de Teste (Carga Seed)](#-usuários-de-teste-carga-seed)
5. [Interface de Documentação (Swagger)](#-interface-de-documentação-swagger)
6. [Fluxo Principal de Negócios](#-fluxo-principal-de-negócios)
7. [Rotas e Endpoints da API](#-rotas-e-endpoints-da-api)
8. [Estratégia de Testes Automatizados](#-estratégia-de-testes-automatizados)
9. [Segurança e Diretrizes LGPD](#-segurança-e-diretrizes-lgpd)

---

## 🏛️ Descrição e Arquitetura

O projeto foi desenvolvido para atender às necessidades de uma rede de lanchonetes com pratos típicos nordestinos, processando pedidos originados de diversos canais físicos e digitais (APP, TOTEM, BALCÃO, PICKUP, WEB).

A arquitetura do backend é baseada em uma divisão limpa por camadas que isolam as responsabilidades:
- **API (Controllers e Handlers):** Trata a recepção de requisições, mapeamento de rotas e formatação do tratamento global de exceções.
- **Application (Services e DTOs):** Contém a lógica de orquestração do negócio, regras de validação via Bean Validation e os objetos de transferência de dados (DTOs).
- **Domain (Enums e Exceções):** Detém a lógica essencial de negócio e representações independentes de tecnologia.
- **Infrastructure (Persistence, Security, Mocks):** Modela os dados de banco via JPA/Hibernate, as configurações de segurança JWT/Spring Security e integrações mocks externas.

---

## 🚀 Tecnologias Utilizadas

| Componente | Tecnologia | Racional / Justificativa |
| :--- | :--- | :--- |
| **Linguagem** | Java 17 (LTS) | Uso de recursos modernos como Records e excelente suporte corporativo. |
| **Framework** | Spring Boot 3.2 | Agilidade no desenvolvimento com injeção de dependências e auto-configuração. |
| **Banco Principal** | PostgreSQL | SGBD relacional robusto, estável e amplamente utilizado em produção. |
| **Banco de Testes** | H2 (Em Memória) | Banco volátil, garantindo testes isolados, rápidos e que rodam offline. |
| **Segurança** | Spring Security + JWT | Autenticação stateless baseada em tokens assinados digitalmente. |
| **Documentação** | SpringDoc OpenAPI 2.3 | Swagger UI dinâmico gerado de forma declarativa a partir do código. |
| **Ambiente** | Docker & Compose | Padronização do ambiente local, evitando incompatibilidade de dependências. |
| **Build** | Maven | Gerenciador de ciclo de vida e dependências clássico do ecossistema Java. |

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos
- **Docker** e **Docker Compose** instalados (Recomendado)
- Ou alternativamente: **Java 17 (JDK)**, **Maven 3.6+** e uma instância local do **PostgreSQL 15**.

---

### 1. Execução via Docker (Recomendado)

O Docker compose configura automaticamente a instância do PostgreSQL e compila a aplicação Spring Boot, mapeando tudo em uma rede isolada.

1. **Clonar o Repositório:**
   ```bash
   git clone https://github.com/MatheusBessado/raizes-do-nordeste.git
   cd raizes-do-nordeste
   ```

2. **Subir os Containers:**
   ```bash
   docker-compose up --build
   ```

3. **Verificar a Inicialização:**
   Aguarde os logs de compilação do Maven no terminal. Quando concluído, você verá a mensagem de inicialização bem-sucedida e o sumário do seed.
   O servidor estará disponível na porta `8080`.

---

### 2. Execução Local Manual

Caso queira rodar sem Docker, siga os passos abaixo:

1. **Configurar o Banco de Dados:**
   Acesse seu cliente PostgreSQL e crie o schema:
   ```sql
   CREATE DATABASE raizes_nordeste;
   ```

2. **Configurar as Variáveis de Ambiente:**
   Crie ou edite o arquivo `.env` na raiz do projeto com base no `.env.example`:
   ```properties
   DB_URL=jdbc:postgresql://localhost:5432/raizes_nordeste
   DB_USER=seu_usuario_postgres
   DB_PASSWORD=sua_senha_postgres
   JWT_SECRET=raizesnordeste-secret-key-super-segura-2026-backend-atividade-local
   JWT_EXPIRATION=86400000
   ```

3. **Compilar e Rodar o Projeto:**
   ```bash
   # Linux/macOS
   export $(cat .env | xargs)
   mvn clean install
   mvn spring-boot:run

   # Windows (PowerShell)
   $env:DB_URL="jdbc:postgresql://localhost:5432/raizes_nordeste"
   $env:DB_USER="postgres"
   $env:DB_PASSWORD="sua_senha"
   $env:JWT_SECRET="raizesnordeste-secret-key-super-segura-2026-backend-atividade-local"
   $env:JWT_EXPIRATION="86400000"
   mvn clean install
   mvn spring-boot:run
   ```

---

## 👥 Usuários de Teste (Carga Seed)

A aplicação conta com um populador automático (`DataSeeder`) que cria os dados iniciais do cardápio, estoque e os seguintes usuários para testes imediatos:

| Perfil (Role) | E-mail | Senha | Pontos Fidelidade Iniciais |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin@raizes.com` | `admin123` | 0 |
| **GERENTE** | `gerente@raizes.com` | `gerente123` | 0 |
| **COZINHA** | `cozinha@raizes.com` | `cozinha123` | 0 |
| **ATENDENTE** | `atendente@raizes.com` | `atendente123` | 0 |
| **CLIENTE** | `cliente@raizes.com` | `cliente123` | 500 pontos |

---

## 📖 Interface de Documentação (Swagger)

A API possui uma interface interativa para testes de requisições:
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Como realizar requisições autenticadas no Swagger:
1. Realize uma chamada em `POST /auth/login` informando um dos e-mails e senhas de teste.
2. Copie o valor de `accessToken` retornado na resposta do JSON.
3. No topo da página do Swagger, clique em **Authorize** (botão com cadeado).
4. Insira no campo: `Bearer {TOKEN_COPIADO}` (sem as chaves, respeitando o espaço após a palavra Bearer).
5. Clique em **Authorize** e feche o modal. As requisições subsequentes incluirão o cabeçalho HTTP de segurança.

---

## 🔄 Fluxo Principal de Negócios

O fluxo crítico da aplicação engloba a criação de pedidos com reserva e validação transacional de estoque, intermediação com o gateway financeiro mockado e a recompensa em fidelidade:

```
[Cliente Autenticado]
        │
        ▼
1. POST /pedidos  ──────► Valida disponibilidade física dos produtos por unidade
        │                 Calcula subtotal e total da venda de forma transacional
        │                 Bloqueia/Reserva a quantidade física no estoque
        │                 Gera Pedido com status "AGUARDANDO_PAGAMENTO"
        │
        ▼
2. POST /pagamentos/callback ◄── Simula callback assíncrono do gateway
        │                       Se aprovado: Atualiza status do pedido para "PAGAMENTO_CONFIRMADO"
        │                       Acumula 1 ponto de fidelidade para cada R$1.00 gasto
        │                       Se recusado: Cancela o pedido e estorna itens para o estoque
        │
        ▼
3. PATCH /pedidos/{id}/status ◄── Cozinha avança status (EM_PREPARO -> PRONTO)
        │
        ▼
4. PATCH /pedidos/{id}/status ◄── Atendente muda status para (ENTREGUE)
```

---

## 🔀 Rotas e Endpoints da API

### Autenticação (`/auth`)
- `POST /auth/cadastro` - Cria um novo cadastro. *(Público. Exige consentimentoFidelidade = true para integrar ao programa de pontos)*
- `POST /auth/login` - Valida credenciais e gera token JWT. *(Público)*

### Unidades (`/unidades`)
- `GET /unidades` - Lista todas as filiais ativas. *(Qualquer usuário autenticado)*
- `GET /unidades/{id}` - Exibe dados de uma filial específica. *(Qualquer usuário autenticado)*
- `POST /unidades` - Cria uma nova unidade. *(Permissão: ADMIN)*
- `PUT /unidades/{id}` - Atualiza dados cadastrais ou ativação de uma unidade. *(Permissão: ADMIN)*
- `DELETE /unidades/{id}` - Inativa uma unidade (Soft Delete). *(Permissão: ADMIN)*

### Produtos (`/produtos`)
- `GET /produtos` - Consulta catálogo paginado com filtro por categoria. *(Qualquer usuário autenticado)*
- `GET /produtos/{id}` - Detalhes do produto. *(Qualquer usuário autenticado)*
- `POST /produtos` - Cadastra novo produto. *(Permissão: ADMIN, GERENTE)*
- `PUT /produtos/{id}` - Atualiza informações do produto. *(Permissão: ADMIN, GERENTE)*
- `DELETE /produtos/{id}` - Inativa um produto no catálogo (Soft Delete). *(Permissão: ADMIN, GERENTE)*

### Estoque (`/estoque`)
- `GET /estoque/unidades/{unidadeId}` - Exibe a situação do estoque da filial. *(Permissão: ADMIN, GERENTE, COZINHA)*
- `POST /estoque/unidades/{unidadeId}/movimentar` - Executa a movimentação manual de "ENTRADA" ou "SAIDA" de insumos/produtos. *(Permissão: ADMIN, GERENTE, COZINHA)*

### Pedidos (`/pedidos`)
- `POST /pedidos` - Inicia a criação de um pedido vinculando itens, unidade e o canal obrigatório. *(Permissão: CLIENTE, ATENDENTE)*
- `GET /pedidos` - Lista e filtra pedidos por canal ou status de forma paginada. *(Qualquer usuário autenticado)*
- `GET /pedidos/{id}` - Busca detalhes e itens de um pedido. *(Qualquer usuário autenticado)*
- `PATCH /pedidos/{id}/status` - Atualiza a etapa do pedido (`EM_PREPARO`, `PRONTO`, `ENTREGUE`). *(Permissão: ADMIN, GERENTE, COZINHA, ATENDENTE)*

### Pagamentos (`/pagamentos`)
- `POST /pagamentos/callback` - Recebe resposta assíncrona do processamento financeiro. *(Público / Gateway)*

### Fidelidade (`/fidelidade`)
- `GET /fidelidade/meu-saldo` - Consulta extrato de pontos do usuário autenticado. *(Qualquer cliente autenticado)*

### Usuários e LGPD (`/usuarios`)
- `PATCH /usuarios/{id}/anonimizar` - Anonimiza as informações pessoais do cliente (Nome, E-mail, SenhaHash, CPF) substituindo por hashes aleatórios e inativa a conta, respeitando o "Direito ao Esquecimento" da LGPD. *(Permissão: ADMIN)*

---

## 🧪 Estratégia de Testes Automatizados

O projeto utiliza **JUnit 5**, **Mockito** e **MockMvc** para testar as camadas críticas do sistema sem acoplamento externo. Os testes são executados sob o perfil de configuração `test`, usando um banco H2 simulado.

### Execução dos Testes
Para rodar a suíte inteira de testes, utilize o comando:
```bash
mvn test
```

### Cobertura e Cenários Validados:
1. **Contexto Geral:** Validação se o Spring Context carrega todas as dependências perfeitamente.
2. **Serviços de Negócio:**
   - `AuthServiceTest`: Validação de login com credenciais válidas/inválidas e controle de e-mail duplicado no cadastro.
   - `PedidoServiceTest`: Validação da criação de pedidos, cálculo transacional de valores, bloqueio de estoque e regras de pontos.
3. **Controladores da API (Integração):**
   - `AuthControllerTest`: Simulação de login real enviando JSON e validando a estrutura de expiração do JWT.
   - `PedidoControllerTest`: Testes integrados com `@WithMockUser` enviando pedidos e validando o subtotal calculado e status de retorno.

---

## 🔒 Segurança e Diretrizes LGPD

Em consonância com as boas práticas de Engenharia de Software e a legislação vigente (LGPD):
- **Criptografia de Senhas:** Nenhuma senha é salva em formato legível. O hash é gerado com o algoritmo **BCrypt** de alta segurança.
- **Tratamento de Dados Pessoais:** O cliente deve explicitamente fornecer o consentimento de fidelidade para ativar o acúmulo de pontos.
- **Direito de Eliminação:** O endpoint `/usuarios/{id}/anonimizar` realiza a limpeza definitiva dos dados do usuário, mantendo apenas informações anonimizadas para fins de relatórios de auditoria interna.
- **Segurança de Acesso:** Cada rota da API possui permissão granular vinculada às atribuições reais de cada ator do sistema (`CLIENTE`, `COZINHA`, `ATENDENTE`, `GERENTE`, `ADMIN`).
