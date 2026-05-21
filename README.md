# 🌵 Raízes do Nordeste — API Back-End

> Projeto Multidisciplinar — Trilha Back-End — UNINTER 2026  
> Rede de lanchonetes "Raízes do Nordeste"

---

## Sobre o projeto

API REST desenvolvida para o projeto multidisciplinar da UNINTER. O sistema gerencia pedidos de uma rede de lanchonetes que atende por varios canais (APP, TOTEM, BALCÃO, PICKUP e WEB).

O fluxo principal implementado foi: **Pedido → Validação de Estoque → Pagamento Mock → Atualização de Status**

A arquitetura segue uma divisão em camadas inspirada no Clean Architecture, separando dominio, aplicação, infraestrutura e API.

---

## Tecnologias utilizadas

- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Swagger/OpenAPI (springdoc)
- Maven

---

## ⚙️ Como rodar o projeto

### 1. Clonar

```bash
git clone https://github.com/SEU_USUARIO/raizes-do-nordeste.git
cd raizes-do-nordeste
```

### 2. Configurar o banco

Criar o banco no PostgreSQL:

```sql
CREATE DATABASE raizes_nordeste;
```

### 3. Configurar as variaveis de ambiente

```bash
cp .env.example .env
```

Editar o `.env` com os dados do seu banco:

```env
DB_URL=jdbc:postgresql://localhost:5432/raizes_nordeste
DB_USER=postgres
DB_PASSWORD=<sua_senha_aqui>
JWT_SECRET=<seu_segredo_jwt_aqui>
JWT_EXPIRATION=86400000
```

### 4. Instalar dependencias

```bash
mvn clean install -DskipTests
```

### 5. Executar

```bash
# exportar as variaveis primeiro
export DB_URL=jdbc:postgresql://localhost:5432/raizes_nordeste
export DB_USER=postgres
export DB_PASSWORD=<sua_senha_aqui>
export JWT_SECRET=<seu_segredo_jwt_aqui>

mvn spring-boot:run
```

API disponivel em: **http://localhost:8080**

---

## Seed automático

Na primeira execução o sistema já cria os dados de teste automaticamente:

| Usuário | E-mail | Perfil |
|---|---|---|
| Administrador | admin@raizesnordeste.com | ADMIN |
| Maria Silva | maria@email.com | CLIENTE |
| João Cozinha | cozinha@raizesnordeste.com | COZINHA |

*(As senhas de teste foram omitidas por questões de segurança)*

Também são criadas 2 unidades (Fortaleza e Recife) e 3 produtos com estoque.

---

## Documentação (Swagger)

Depois de rodar a aplicação acessar:

**http://localhost:8080/swagger-ui.html**

Para testar os endpoints autenticados:
1. Fazer login em `POST /auth/login`
2. Copiar o `accessToken` da resposta
3. Clicar em **Authorize** no Swagger e colocar `Bearer <token>`

Link da documentacao JSON: http://localhost:8080/api-docs

---

## Como testar com Postman

Importar o arquivo `raizes-nordeste.postman_collection.json` no Postman.

**Ordem sugerida para executar:**
1. `Auth > T01 - Login válido (Admin)` — salva o token automaticamente
2. `Auth > T02 - Login válido (Cliente)` — salva o token do cliente
3. `Unidades > T06 - Listar unidades`
4. `Estoque > T08 - Consultar estoque unidade 1`
5. `Pedidos > T10 - Criar pedido (APP)` — fluxo completo com pagamento mock
6. `Pedidos > T13 - Atualizar status`
7. Pasta `Erros` — testa os cenarios negativos

OBS: os testes de criacao de pedido usam o token do cliente (variavel `tokenCliente`)

---

## Estrutura do projeto

```
src/main/java/com/raizesnordeste/
├── domain/
│   ├── enums/        # CanalPedido, StatusPedido, PerfilUsuario, StatusPagamento
│   └── exception/    # NegocioException, RecursoNaoEncontradoException
├── application/
│   ├── service/      # AuthService, PedidoService, EstoqueService, etc
│   └── dto/request/  # DTOs de entrada validados com Bean Validation
├── infrastructure/
│   ├── persistence/  # Entidades JPA e Repositories
│   ├── security/     # JWT, Spring Security, Auditoria
│   └── mock/         # GatewayPagamentoMock
└── api/
    ├── controller/   # Controllers REST
    └── handler/      # Tratamento global de erros
```

---

## Segurança e LGPD

- Senhas armazenadas com **BCrypt** (nunca em texto puro)
- Autenticação via **JWT** sem sessão (stateless)
- Controle de acesso por perfil: ADMIN, GERENTE, ATENDENTE, COZINHA, CLIENTE
- Campo `consentimentoFidelidade` no cadastro para cumprir requisito da LGPD
- Respostas da API nunca retornam o hash da senha
- Log de auditoria registra ações sensíveis (login, criação de pedido, mudança de status)

---

## Observacoes

- O pagamento mock recusa pedidos com valor acima de R$500 (pra poder testar o fluxo de recusa)
- O campo `canalPedido` é obrigatório na criação de pedido (ENUM: APP, TOTEM, BALCAO, PICKUP, WEB)
- A filtragem de pedidos por canal funciona via query param: `GET /pedidos?canalPedido=APP`
