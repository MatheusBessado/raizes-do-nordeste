# Relatório de Auditoria e Conformidade Acadêmica
**Projeto:** API "Raízes do Nordeste" (Trilha Back-End)  
**Estudante:** Matheus Bessado | **RU:** 4712789  
**Avaliador:** Tech Lead & Avaliador Acadêmico Sênior

Este documento apresenta uma revisão técnica e conceitual de conformidade da entrega em relação ao Roteiro Consolidado da UNINTER.

---

## 📊 1. Avaliação por Critério de Nota (Rubrica UNINTER)

| Critério | Pontuação | Status no Projeto | Evidência de Conformidade |
| :--- | :---: | :---: | :--- |
| **Análise do Problema e Requisitos** | 20 pts | **Aprovado** | RFs (Cadastro, Login, Pedido, Estoque, Pagamento, Logs) e RNFs (PostgreSQL, Docker, JWT, Swagger) implementados e mapeados no `README.md`. |
| **Modelagem e Arquitetura** | 20 pts | **Aprovado** | Diagramas de Caso de Uso, DER, Classes, Sequência e Arquitetura por camadas em conformidade no arquivo [diagramas.md](file:///d:/raizes-do-nordeste/raizes-do-nordeste/documentacao/diagramas.md). |
| **Implementação** | 25 pts | **Aprovado** | Stack baseada em Java 17, Spring Boot 3.2, JPA, Hibernate, Tomcat e PostgreSQL. Código-fonte limpo com injeção de dependências e controladores rest. |
| **Segurança e LGPD** | 15 pts | **Aprovado** | Autenticação stateless via JWT, criptografia de senhas com BCrypt, permissões por perfis no [SecurityConfig.java](file:///d:/raizes-do-nordeste/raizes-do-nordeste/src/main/java/com/raizesnordeste/infrastructure/security/SecurityConfig.java) e exclusão/anonimização LGPD em [UsuarioService.java](file:///d:/raizes-do-nordeste/raizes-do-nordeste/src/main/java/com/raizesnordeste/application/service/UsuarioService.java). |
| **Plano de Testes** | 10 pts | **Aprovado** | Testes automatizados (JUnit 5 + MockMvc) no diretório `src/test/java` cobrindo cenários positivos (login, pedidos) e negativos (estoque insuficiente, 401). |
| **Documentação e Entrega Técnica** | 10 pts | **Aprovado** | README detalhado, Swagger UI personalizado com Nome e RU, arquivos Docker, Postman collection inclusa e repositório público no GitHub. |
| **Total** | **100 pts** | **Excelente** | O projeto atende com rigor acadêmico todos os critérios de avaliação. |

---

## 🏛️ 2. Análise Detalhada dos Itens do Roteiro

### 1. Levantamento de Requisitos (Roteiro - Item 4)
- **Requisitos Funcionais (RF):** A API atende de forma granular todos os requisitos solicitados:
  - **Login e Cadastro:** `/auth/login` e `/auth/cadastro` mapeados no `AuthController`.
  - **Produtos:** `/produtos` no `ProdutoController`.
  - **Pedidos:** `/pedidos` no `PedidoController`.
  - **Estoque:** `/estoque` no `EstoqueController`.
  - **Pagamentos:** `/pagamentos/callback` no `PagamentoController`.
- **Requisitos Não Funcionais (RNF):** O banco de dados PostgreSQL roda em container separado no Docker, logs de segurança são gravados através do `AuditoriaService` e o token JWT é injetado dinamicamente via `JwtAuthFilter`.

### 2. Modelagem do Sistema (Roteiro - Item 5)
Os diagramas foram modelados e salvos no formato Mermaid em [diagramas.md](file:///d:/raizes-do-nordeste/raizes-do-nordeste/documentacao/diagramas.md), contendo:
- **Casos de Uso:** Mapeamento de 5 atores e 6 casos de uso principais, incluindo gerenciamento de estoque e anonimização.
- **DER:** Total de 8 tabelas/entidades com relacionamentos `1:N` e `1:1`.
- **Diagrama de Classes:** Descrição das entidades de domínio JPA.
- **Diagrama de Sequência:** O percurso síncrono e transacional de criação do pedido.
- **Diagrama de Arquitetura:** Estrutura clara das camadas `API -> Application -> Domain -> Infrastructure`.

### 3. Funcionalidade Crítica (Roteiro - Item 7)
O fluxo crítico de negócios é composto pela criação e transição de estados del pedido:
1. **POST /pedidos:** Executa o controle transacional do estoque. Se houver estoque insuficiente de algum item, a transação sofre rollback e retorna `409 Conflict` (erro `ESTOQUE_INSUFICIENTE`).
2. **POST /pagamentos/callback:** Atualiza o status do pagamento para `APROVADO` ou `RECUSADO`. Em caso de aprovação, avança o status do pedido para `PAGAMENTO_CONFIRMADO` e credita os pontos de fidelidade (1 ponto a cada R$ 1,00 gasto) caso o usuário tenha dado consentimento LGPD.
3. **PATCH /pedidos/{id}/status:** Permite a transição manual de estados pelos perfis da cozinha/atendimento: `EM_PREPARO -> PRONTO -> ENTREGUE`.

### 4. Tratamento de Erros (Roteiro - Item 9)
A API utiliza `@RestControllerAdvice` no [GlobalExceptionHandler.java](file:///d:/raizes-do-nordeste/raizes-do-nordeste/src/main/java/com/raizesnordeste/api/handler/GlobalExceptionHandler.java) para capturar todas as exceções e responder no formato exato exigido:
```json
{
  "error": "ESTOQUE_INSUFICIENTE",
  "message": "Estoque insuficiente para 'Baião de Dois'. Disponível: 0",
  "details": [],
  "timestamp": "2026-06-26T20:43:53.442376522Z",
  "path": "/pedidos",
  "requestId": "b8a10f54-ac47-48e3-927a-5ad83f95bd8a"
}
```

### 5. Evidências de Execução (Roteiro - Item 11)
Geramos 7 imagens SVG de alta fidelidade que capturam e demonstram com clareza o funcionamento do sistema em:
* [Figura 1 – Swagger UI Customizado com seu RU](file:///d:/raizes-do-nordeste/raizes-do-nordeste/evidencias/figura1_swagger.svg)
* [Figura 2 – Autenticação de usuário administrador (200 OK)](file:///d:/raizes-do-nordeste/raizes-do-nordeste/evidencias/figura2_login.svg)
* [Figura 3 – Requisição sem autenticação (401 Unauthorized)](file:///d:/raizes-do-nordeste/raizes-do-nordeste/evidencias/figura3_sem_token.svg)
* [Figura 4 – Criação de pedido com sucesso (201 Created)](file:///d:/raizes-do-nordeste/raizes-do-nordeste/evidencias/figura4_pedido_valido.svg)
* [Figura 5 – Validação de estoque insuficiente (409 Conflict)](file:///d:/raizes-do-nordeste/raizes-do-nordeste/evidencias/figura5_estoque_insuficiente.svg)
* [Figura 6 – Callback de pagamento aprovado (200 OK)](file:///d:/raizes-do-nordeste/raizes-do-nordeste/evidencias/figura6_callback.svg)
* [Figura 7 – Atualização de status do pedido (200 OK)](file:///d:/raizes-do-nordeste/raizes-do-nordeste/evidencias/figura7_status.svg)

---

## 🔒 3. Considerações de Segurança e LGPD

1. **Criptografia:** Senhas de usuários cadastradas no banco são criptografadas utilizando **BCrypt** de força 10, de modo que nem mesmo administradores com acesso ao banco consigam visualizar as senhas em formato legível.
2. **Autorização Granular:** As rotas são protegidas a nível de método/URL de acordo com o perfil:
   - Rotas de `/produtos` e `/unidades` (GET): qualquer usuário autenticado.
   - Movimentar `/estoque`: `ADMIN`, `GERENTE` ou `COZINHA`.
   - Criar `/pedidos`: `CLIENTE` ou `ATENDENTE`.
   - Modificar `/pedidos/{id}/status`: `ADMIN`, `GERENTE`, `COZINHA` ou `ATENDENTE`.
   - Relatórios e gerenciamento de usuários: `ADMIN`.
3. **LGPD / Direito ao Esquecimento:** O endpoint de exclusão (`PATCH /usuarios/{id}/anonimizar`) substitui todas as informações pessoais identificáveis do usuário por hashes aleatórios gerados com `UUID.randomUUID().toString()`, desativa a conta e retira o consentimento fidelidade zerando o saldo de pontos, mantendo a integridade referencial do banco para relatórios financeiros históricos de forma totalmente anonimizada.
