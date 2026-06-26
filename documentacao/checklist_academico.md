# Checklist Acadêmico - Entrega do Projeto Multidisciplinar
**Curso: Análise e Desenvolvimento de Sistemas (UNINTER)**  
**Estudante:** Matheus Bessado | **RU:** 4712789  
**Projeto:** API REST "Raízes do Nordeste" (Trilha Back-End)

---

## 🏛️ 1. Manual de Captura de Evidências (Swagger e Postman)

Para documentar os testes em seu arquivo ABNT, use as imagens programaticamente geradas na pasta `evidencias/` de seu workspace ou siga os passos manuais abaixo:

### Passo a Passo no Swagger UI
1. **Inicialização:** Suba a aplicação via Docker (`docker-compose up --build`).
2. **Acesso:** Abra o navegador e digite: `http://localhost:8080/swagger-ui.html`.
3. **Verificação de Identidade:** Confirme se o título/descrição exibe:  
   *"Desenvolvido por Matheus Bessado (RU: 4712789) - UNINTER 2026."* (Isso comprova a autoria).
4. **Captura:** Tire um print geral mostrando os grupos `/auth`, `/pedidos`, `/produtos`, `/estoque`, `/pagamentos`, `/fidelidade`, `/usuarios`.

### Passo a Passo no Postman
Para cada cenário, execute a chamada, selecione a aba **Pretty** da resposta e capture o cabeçalho superior (Método + URL + Status Code) e o rodapé inferior (JSON de resposta).

- **T01 (Login Admin):** 
  - `POST /auth/login` com email `admin@raizes.com` e senha `admin123`.
  - Evidência: Retorno `200 OK` contendo o `accessToken` do tipo `Bearer`.
- **T02 (Sem Token):** 
  - `GET /pedidos` sem incluir o token de autorização.
  - Evidência: Retorno `401 Unauthorized` e JSON `{"error": "NAO_AUTENTICADO", ...}`.
- **T05 (Criação de Pedido APP):** 
  - `POST /pedidos` com Token de Cliente no Header (`Bearer {Token}`) e JSON no body indicando `canalPedido: "APP"`.
  - Evidência: Retorno `201 Created` contendo o `pedidoId` e status `AGUARDANDO_PAGAMENTO`.
- **T07 (Estoque Insuficiente):** 
  - `POST /pedidos` com Token de Cliente e corpo solicitando o Produto 2 (Baião de Dois) na Unidade 2 (Recife), que está com estoque zerado.
  - Evidência: Retorno `409 Conflict` e JSON com erro `ESTOQUE_INSUFICIENTE`.
- **T08 (Callback Aprovado):** 
  - `POST /pagamentos/callback` contendo o `pedidoId` criado no T05 e `status: "APROVADO"`.
  - Evidência: Retorno `200 OK` mostrando o `novoStatus` como `PAGAMENTO_CONFIRMADO`.
- **T10 (Atualizar Status Pedido):** 
  - `PATCH /pedidos/{id}/status` com Token de Admin e corpo `{"status": "EM_PREPARO"}`.
  - Evidência: Retorno `200 OK` contendo o status atualizado do pedido.

---

## 🔍 2. Auditoria de Configuração do GitHub

Para evitar nota zero na entrega técnica (um dos critérios mais rigorosos da UNINTER), valide os seguintes itens em seu repositório público:

1. **Visibilidade do Repositório:** 
   - [ ] O repositório **DEVE ser público**. Vá em *Settings -> Danger Zone -> Change visibility* e mude para Public. (Se o avaliador receber um link privado, a nota será ZERO sem possibilidade de reenvio).
2. **Estrutura de Pastas limpa:**
   - [ ] Certifique-se de que a raiz contém: `pom.xml`, `Dockerfile`, `docker-compose.yml`, `.env.example`, `.gitignore`, `README.md` e a pasta `src/`.
3. **Gerenciamento de Segredos (.env.example):**
   - [ ] **Nunca suba o arquivo `.env` com senhas reais**. O arquivo `.gitignore` deve incluir `.env`.
   - [ ] O arquivo `.env.example` deve estar na raiz contendo chaves sem os valores confidenciais (ex: `JWT_SECRET=sua-chave-aqui`).
4. **README Completo e Profissional:**
   - [ ] Título claro do projeto e descrição do problema resolvido.
   - [ ] Tabela com as tecnologias, versões e racional de uso.
   - [ ] Instruções detalhadas de execução (via Docker e Maven local).
   - [ ] Lista dos perfis de usuários de teste e credenciais padrão de seed.
   - [ ] Documentação concisa das rotas da API.
5. **Histórico de Commits:**
   - [ ] Evite entrega com apenas 1 commit ("initial commit"). Faça commits incrementais que mostrem a evolução do desenvolvimento.

---

## 📄 3. Regras ABNT e Empacotamento de Envio

Ao compilar o documento final, siga rigorosamente as normas de formatação abaixo:

### Formatação de Figuras (Prints de Tela)
Conforme a ABNT NBR 14724, cada print inserido deve ter:
- **No topo:** Identificação da figura composta pelo nome (Figura X), número de ordem e título descritivo. (Exemplo: *Figura 3 – Resposta da criação de pedido via APP no Postman*).
- **Na parte inferior:** Indicação da fonte consultada (mesmo que seja o próprio autor). (Exemplo: *Fonte: Elaborado pelo autor (2026)*).
- **Legenda e Fonte:** Devem estar centralizadas, em fonte Arial ou Times New Roman tamanho 10.

*Exemplo de estrutura no documento:*
```text
Figura 4 – Resposta de login válido do Admin no Postman
[IMAGEM DO PRINT]
Fonte: Elaborado pelo autor (2026)
```

### Configurações de Página
- **Margens:** Esquerda e Superior: 3 cm | Direita e Inferior: 2 cm.
- **Fontes permitidas:** Arial ou Times New Roman (tamanho 12 para o corpo de texto, tamanho 10 para fontes de figuras/tabelas e tamanho 14 em negrito para títulos de seções principais).
- **Espaçamento:** 1,5 entre linhas.
- **Alinhamento:** Justificado.

### Nomeação e Envio do Arquivo
1. **Conversão:** Salve o arquivo final no formato **PDF**.
2. **Nomenclatura Obrigatória:** O nome do arquivo deve conter o seu RU e o nome do projeto exatamente no formato:  
   `4712789_Projeto_Back_End.pdf`
3. **AVA UNINTER:** Acesse o Ambiente Virtual de Aprendizagem, clique na disciplina do Projeto Multidisciplinar, vá em "Trabalhos" e envie o PDF gerado. Certifique-se de anexar o link correto do seu GitHub no campo de comentários ou na capa do relatório.
