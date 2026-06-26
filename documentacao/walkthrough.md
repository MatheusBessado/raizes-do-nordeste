# Walkthrough — Evidências de Execução (API Raízes do Nordeste)

Neste documento, apresentamos a síntese das ações de Tech Lead realizadas para auditoria, correção do fluxo de autenticação e geração de evidências dinâmicas com os dados reais do aluno Matheus Bessado (RU: 4712789).

---

## 🛠️ Alterações Realizadas no Código

1. **Customização de Swagger UI / OpenAPI:**
   - Modificado [OpenApiConfig.java](file:///d:/raizes-do-nordeste/raizes-do-nordeste/src/main/java/com/raizesnordeste/infrastructure/security/OpenApiConfig.java) para injetar o nome e RU do aluno nas propriedades de documentação, garantindo a autoria do projeto.
2. **Correção de Acesso sem Token (401 vs 403):**
   - Modificado [SecurityConfig.java](file:///d:/raizes-do-nordeste/raizes-do-nordeste/src/main/java/com/raizesnordeste/infrastructure/security/SecurityConfig.java) configurando um `AuthenticationEntryPoint` para retornar `401 Unauthorized` em acessos sem token aos endpoints restritos.
   - Atualizado o teste integrado em [PedidoControllerTest.java](file:///d:/raizes-do-nordeste/raizes-do-nordeste/src/test/java/com/raizesnordeste/api/controller/PedidoControllerTest.java) para validar a resposta `401` em vez de `403`.

---

## 📸 Evidências de Execução Geradas

As imagens abaixo foram geradas programaticamente a partir da execução real contra os containers do Docker. Você pode copiá-las e inseri-las diretamente em seu relatório do Word/LibreOffice.

### 🌐 Swagger UI Customizado
Contém a identificação do aluno no cabeçalho geral e a listagem de rotas.
![Swagger UI Customizado](/C:/Users/mathe/.gemini/antigravity/brain/fc416655-8e3e-4a74-aad1-6141f4637da3/evidencias/swagger_ui.svg)

---

### 📬 Testes de Integração via Postman

#### T01 — Login válido (Admin)
Autenticação utilizando credenciais de seed no endpoint `/auth/login`. Retorna `200 OK` e o token JWT correspondente.
![T01 - Login válido Admin](/C:/Users/mathe/.gemini/antigravity/brain/fc416655-8e3e-4a74-aad1-6141f4637da3/evidencias/t01_postman.svg)

#### T02 — Erro 401 sem token
Acesso restrito ao endpoint `/pedidos` sem cabeçalho Authorization. Retorna `401 Unauthorized`.
![T02 - Acesso Sem Token](/C:/Users/mathe/.gemini/antigravity/brain/fc416655-8e3e-4a74-aad1-6141f4637da3/evidencias/t02_postman.svg)

#### T05 — Criação de Pedido com canalPedido APP
Criação de pedido para o Cliente com sucesso via canal `APP`. Retorna `201 Created` e gera o `pedidoId: 2`.
![T05 - Criar Pedido APP](/C:/Users/mathe/.gemini/antigravity/brain/fc416655-8e3e-4a74-aad1-6141f4637da3/evidencias/t05_postman.svg)

#### T07 — Erro 409 Estoque Insuficiente
Tentativa de criação de pedido para o produto 2 (Baião de Dois) na Unidade 2 (Recife), que possui estoque zerado. Retorna `409 Conflict` e erro `ESTOQUE_INSUFICIENTE`.
![T07 - Estoque Insuficiente](/C:/Users/mathe/.gemini/antigravity/brain/fc416655-8e3e-4a74-aad1-6141f4637da3/evidencias/t07_postman.svg)

#### T08 — Callback Pagamento Aprovado
Envio de callback do gateway mock para aprovação do pedido 2. Retorna `200 OK` e atualiza para `PAGAMENTO_CONFIRMADO`.
![T08 - Callback Pagamento](/C:/Users/mathe/.gemini/antigravity/brain/fc416655-8e3e-4a74-aad1-6141f4637da3/evidencias/t08_postman.svg)

#### T10 — Atualizar status pedido
Atualização de status do pedido para `EM_PREPARO` realizada pelo Admin. Retorna `200 OK`.
![T10 - Atualizar Status](/C:/Users/mathe/.gemini/antigravity/brain/fc416655-8e3e-4a74-aad1-6141f4637da3/evidencias/t10_postman.svg)
