# Diagramas do Projeto — Raízes do Nordeste

Este arquivo contém as especificações dos diagramas do projeto em formato **Mermaid**. Você pode visualizá-los diretamente em visualizadores de Markdown compatíveis, exportá-los no [Mermaid Live Editor](https://mermaid.live/) ou importá-los no [Draw.io](https://app.diagrams.net/) (via *+ -> Advanced -> Mermaid*).

---

## 👥 1. Diagrama de Casos de Uso (Use Case)

Representa os atores do sistema e os principais fluxos de negócio que cada um realiza:

```mermaid
graph TD
    %% Atores
    Cliente["Cliente (Estudante)"]
    Atendente["Atendente"]
    Cozinha["Cozinheiro"]
    Gerente["Gerente"]
    Admin["Administrador"]

    %% Casos de Uso
    UC_Login["Realizar Autenticação (JWT)"]
    UC_Pedido["Criar Pedido (Multicanal)"]
    UC_Status["Avançar Status de Preparo"]
    UC_Estoque["Movimentar Estoque"]
    UC_Fidelidade["Consultar Pontos de Fidelidade"]
    UC_Anonimizar["Anonimizar Cadastro (LGPD)"]

    %% Associações
    Cliente --> UC_Login
    Cliente --> UC_Pedido
    Cliente --> UC_Fidelidade

    Atendente --> UC_Login
    Atendente --> UC_Pedido
    Atendente --> UC_Status

    Cozinha --> UC_Login
    Cozinha --> UC_Status
    Cozinha --> UC_Estoque

    Gerente --> UC_Login
    Gerente --> UC_Estoque
    Gerente --> UC_Status

    Admin --> UC_Login
    Admin --> UC_Estoque
    Admin --> UC_Status
    Admin --> UC_Anonimizar
```

---

## 🗄️ 2. Modelo Entidade-Relacionamento (DER)

Modelagem relacional do banco de dados PostgreSQL contendo os relacionamentos e cardinalidades:

```mermaid
erDiagram
    USUARIO ||--o{ PEDIDO : "realiza"
    USUARIO ||--o| FIDELIDADE : "possui"
    UNIDADE ||--o{ ESTOQUE : "armazena"
    PRODUTO ||--o{ ESTOQUE : "contem"
    PEDIDO ||--o{ ITEM_PEDIDO : "contem"
    PRODUTO ||--o{ ITEM_PEDIDO : "inclui"
    PEDIDO ||--o| PAGAMENTO : "gera"
    USUARIO ||--o{ LOG_AUDITORIA : "gera"

    USUARIO {
        Long id PK
        String nome
        String email
        String senhaHash
        String perfil
        Boolean ativo
        Boolean consentimentoFidelidade
    }
    PEDIDO {
        Long id PK
        String canalPedido
        BigDecimal total
        String status
        Instant criadoEm
    }
    ITEM_PEDIDO {
        Long id PK
        Integer quantidade
        BigDecimal precoUnitario
        BigDecimal subtotal
    }
    PRODUTO {
        Long id PK
        String nome
        String descricao
        BigDecimal preco
        String categoria
        Boolean disponivel
    }
    ESTOQUE {
        Long id PK
        Integer quantidade
    }
    UNIDADE {
        Long id PK
        String nome
        String endereco
        String cidade
        String estado
        Boolean ativa
    }
    PAGAMENTO {
        Long id PK
        String transacaoId
        String status
        String mensagem
    }
    FIDELIDADE {
        Long id PK
        Integer saldoPontos
    }
    LOG_AUDITORIA {
        Long id PK
        String acao
        String detalhes
        Instant timestamp
    }
```

---

## ☕ 3. Diagrama de Classes (Camada Domain/Entities)

Diagrama das entidades de persistência JPA com seus tipos Java correspondentes e relacionamentos de orientação a objetos:

```mermaid
classDiagram
    class UsuarioEntity {
        +Long id
        +String nome
        +String email
        +String senhaHash
        +PerfilUsuario perfil
        +Boolean ativo
        +Boolean consentimentoFidelidade
    }
    class PedidoEntity {
        +Long id
        +CanalPedido canalPedido
        +BigDecimal total
        +StatusPedido status
        +Instant criadoEm
        +UsuarioEntity usuario
        +UnidadeEntity unidade
        +List~ItemPedidoEntity~ itens
        +PagamentoEntity pagamento
    }
    class ItemPedidoEntity {
        +Long id
        +PedidoEntity pedido
        +ProdutoEntity produto
        +Integer quantidade
        +BigDecimal precoUnitario
        +BigDecimal subtotal
    }
    class ProdutoEntity {
        +Long id
        +String nome
        +String descricao
        +BigDecimal preco
        +String categoria
        +Boolean disponível
    }
    class EstoqueEntity {
        +Long id
        +UnidadeEntity unidade
        +ProdutoEntity produto
        +Integer quantidade
    }
    class UnidadeEntity {
        +Long id
        +String nome
        +String endereco
        +String cidade
        +String estado
        +Boolean ativa
    }
    class PagamentoEntity {
        +Long id
        +PedidoEntity pedido
        +String transacaoId
        +StatusPagamento status
        +String mensagem
    }
    class FidelidadeEntity {
        +Long id
        +UsuarioEntity usuario
        +Integer saldoPontos
    }
    class LogAuditoriaEntity {
        +Long id
        +UsuarioEntity usuario
        +String acao
        +String detalhes
        +Instant timestamp
    }

    PedidoEntity "1" *-- "many" ItemPedidoEntity
    PedidoEntity "many" o-- "1" UsuarioEntity
    PedidoEntity "many" o-- "1" UnidadeEntity
    PedidoEntity "1" -- "0..1" PagamentoEntity
    ItemPedidoEntity "many" o-- "1" ProdutoEntity
    EstoqueEntity "many" o-- "1" UnidadeEntity
    EstoqueEntity "many" o-- "1" ProdutoEntity
    FidelidadeEntity "1" -- "1" UsuarioEntity
    LogAuditoriaEntity "many" o-- "1" UsuarioEntity
```

---

## 🔄 4. Diagrama de Sequência (Fluxo de Criação de Pedido)

O percurso síncrono e transacional desde o envio do JSON pelo Cliente até o fechamento financeiro mockado e crédito de pontos de fidelidade:

```mermaid
sequenceDiagram
    actor Cliente
    participant PedidoController
    participant PedidoService
    participant EstoqueService
    participant GatewayPagamentoMock
    participant Pagamento
    participant Fidelidade

    Cliente->>PedidoController: POST /pedidos (itens, canal, pagamento)
    activate PedidoController
    PedidoController->>PedidoService: criarPedido(request, emailCliente)
    activate PedidoService
    PedidoService->>EstoqueService: validarEReservarEstoque(itens, unidadeId)
    activate EstoqueService
    EstoqueService-->>PedidoService: estoque reservado (sucesso)
    deactivate EstoqueService
    
    PedidoService->>PedidoService: calcularTotalEPontos()
    PedidoService->>GatewayPagamentoMock: processarPagamento(pedido)
    activate GatewayPagamentoMock
    GatewayPagamentoMock-->>PedidoService: pagamento aprovado/recusado
    deactivate GatewayPagamentoMock
    
    alt Pagamento Aprovado
        PedidoService->>Pagamento: registrarPagamento(APROVADO)
        PedidoService->>Fidelidade: creditarPontos(cliente, valorTotal)
    else Pagamento Recusado
        PedidoService->>Pagamento: registrarPagamento(RECUSADO)
        PedidoService->>EstoqueService: liberarEstoque(itens, unidadeId)
    end
    
    PedidoService-->>PedidoController: PedidoEntity
    deactivate PedidoService
    PedidoController-->>Cliente: 201 Created (JSON Pedido)
    deactivate PedidoController
```

---

## 🏛️ 5. Diagrama de Arquitetura (Divisão por Camadas)

Visão geral da estrutura de pacotes da API, demonstrando o fluxo limpo de dependências:

```mermaid
graph TD
    subgraph API [API / Presentation Layer]
        Controllers
        Handlers[GlobalExceptionHandler]
    end
    subgraph Application [Application / Service Layer]
        Services
        DTOs
    end
    subgraph Domain [Domain / Entities Layer]
        Enums
        Exceptions
    end
    subgraph Infrastructure [Infrastructure Layer]
        Security[Security / JWT]
        Persistence[Repositories / Entities]
        Mocks[Gateway Mocks]
        DataSeeder
    end

    API --> Application
    Application --> Domain
    Infrastructure --> Domain
    Infrastructure --> Application
    Infrastructure --> PostgreSQL[(PostgreSQL Database)]
```
