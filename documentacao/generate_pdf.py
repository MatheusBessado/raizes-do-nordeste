import os
import time
from html2image import Html2Image
from fpdf import FPDF

EVIDENCIAS_DIR = r"d:\raizes-do-nordeste\raizes-do-nordeste\evidencias"
OUTPUT_PDF = r"d:\4712789_Projeto_Back_End.pdf"

# ----------------------------------------------------
# 1. GERAÇÃO DOS DIAGRAMAS MERMAID EM PNG
# ----------------------------------------------------
print("Iniciando renderização de diagramas Mermaid para PNG...")
hti = Html2Image(custom_flags=['--no-sandbox', '--disable-gpu'])

diagrams = {
    "diagrama_caso_uso.png": """
    graph TD
        Cliente["Cliente"]
        Atendente["Atendente"]
        Cozinha["Cozinheiro"]
        Gerente["Gerente"]
        Admin["Administrador"]

        UC_Login["Realizar Autenticação (JWT)"]
        UC_Pedido["Criar Pedido (Multicanal)"]
        UC_Status["Avançar Status de Preparo"]
        UC_Estoque["Movimentar Estoque"]
        UC_Fidelidade["Consultar Pontos"]

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
    """,
    "diagrama_der.png": """
    erDiagram
        USUARIO ||--o{ PEDIDO : "realiza"
        USUARIO ||--o| FIDELIDADE : "possui"
        UNIDADE ||--o{ ESTOQUE : "armazena"
        PRODUTO ||--o{ ESTOQUE : "contem"
        PEDIDO ||--o{ ITEM_PEDIDO : "contem"
        PRODUTO ||--o{ ITEM_PEDIDO : "inclui"
        PEDIDO ||--o| PAGAMENTO : "gera"

        USUARIO {
            Long id PK
            String nome
            String email
            String senhaHash
            String perfil
            Boolean ativo
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
            String cidade
            String estado
        }
        PAGAMENTO {
            Long id PK
            String transacaoId
            String status
        }
        FIDELIDADE {
            Long id PK
            Integer saldoPontos
        }
    """,
    "diagrama_classes.png": """
    classDiagram
        class UsuarioEntity {
            +Long id
            +String nome
            +String email
            +PerfilUsuario perfil
            +Boolean ativo
        }
        class PedidoEntity {
            +Long id
            +CanalPedido canalPedido
            +BigDecimal total
            +StatusPedido status
            +Instant criadoEm
        }
        class ItemPedidoEntity {
            +Long id
            +Integer quantidade
            +BigDecimal precoUnitario
            +BigDecimal subtotal
        }
        class ProdutoEntity {
            +Long id
            +String nome
            +BigDecimal preco
            +String categoria
        }
        class EstoqueEntity {
            +Long id
            +Integer quantidade
        }
        class UnidadeEntity {
            +Long id
            +String nome
            +String cidade
        }
        class PagamentoEntity {
            +Long id
            +String transacaoId
            +StatusPagamento status
        }

        PedidoEntity "1" *-- "many" ItemPedidoEntity
        ItemPedidoEntity "many" o-- "1" ProdutoEntity
        EstoqueEntity "many" o-- "1" UnidadeEntity
        EstoqueEntity "many" o-- "1" ProdutoEntity
    """,
    "diagrama_sequencia.png": """
    sequenceDiagram
        actor Cliente
        participant Controller as PedidoController
        participant Service as PedidoService
        participant Estoque as EstoqueService
        participant PG as GatewayMock

        Cliente->>Controller: POST /pedidos
        activate Controller
        Controller->>Service: criarPedido(req)
        activate Service
        Service->>Estoque: validarEReservarEstoque()
        activate Estoque
        Estoque-->>Service: estoque reservado
        deactivate Estoque
        Service->>PG: processarPagamento()
        activate PG
        PG-->>Service: pagamento aprovado
        deactivate PG
        Service-->>Controller: PedidoEntity
        deactivate Service
        Controller-->>Cliente: 201 Created
        deactivate Controller
    """,
    "diagrama_arquitetura.png": """
    graph TD
        subgraph API [API / Presentation]
            Controllers
            Handlers[GlobalExceptionHandler]
        end
        subgraph Application [Application]
            Services
            DTOs
        end
        subgraph Domain [Domain]
            Enums
            Exceptions
        end
        subgraph Infrastructure [Infrastructure]
            Security[SecurityConfig]
            Persistence[Repositories]
            Mocks[GatewayMock]
        end

        API --> Application
        Application --> Domain
        Infrastructure --> Domain
        Infrastructure --> Application
    """
}

# Render diagrams to PNG using CDN Mermaid
for filename, code in diagrams.items():
    html_content = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <style>
            body {{
                background: white;
                margin: 0;
                padding: 10px;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 95vh;
            }}
            .mermaid {{
                width: 100%;
                max-width: 800px;
            }}
        </style>
        <script src="https://cdn.jsdelivr.net/npm/mermaid/dist/mermaid.min.js"></script>
        <script>
            mermaid.initialize({{ startOnLoad: true, theme: 'neutral' }});
        </script>
    </head>
    <body>
        <div class="mermaid">
            {code}
        </div>
    </body>
    </html>
    """
    hti.size = (800, 500)
    hti.screenshot(html_str=html_content, save_as=filename)
    if os.path.exists(filename):
        os.replace(filename, os.path.join(EVIDENCIAS_DIR, filename))
        print(f"{filename} renderizado com sucesso.")

# ----------------------------------------------------
# 2. DEFINIÇÃO DA CLASSE PDF ABNT
# ----------------------------------------------------
class PDF_ABNT(FPDF):
    def __init__(self):
        super().__init__(orientation="P", unit="mm", format="A4")
        # Margens ABNT: Superior 3cm, Esquerda 3cm, Direita 2cm, Inferior 2cm
        self.set_margins(30, 30, 20)
        self.set_auto_page_break(auto=True, margin=20)
        
        # Carrega fontes Arial padrão do Windows
        self.add_font("Arial", "", r"C:\Windows\Fonts\arial.ttf")
        self.add_font("Arial", "B", r"C:\Windows\Fonts\arialbd.ttf")
        self.add_font("Arial", "I", r"C:\Windows\Fonts\ariali.ttf")

    def header(self):
        # ABNT: Numeração no canto superior direito
        if self.page_no() > 1:
            self.set_font("Arial", "", 10)
            self.cell(0, 10, str(self.page_no()), 0, 0, "R")
            self.ln(10)

    def print_title(self, title, level=1):
        self.set_text_color(0, 0, 0)
        if level == 1:
            self.ln(5)
            self.set_font("Arial", "B", 14)
            self.multi_cell(0, 10, title.upper())
            self.ln(5)
        elif level == 2:
            self.ln(3)
            self.set_font("Arial", "B", 12)
            self.multi_cell(0, 8, title)
            self.ln(3)

    def print_paragraph(self, text, style="", size=12):
        self.set_font("Arial", style, size)
        self.set_text_color(51, 51, 51)
        # Espaçamento 1.5 line spacing (multi_cell h=6 para fonte 12)
        self.multi_cell(0, 6, text, align="J")
        self.ln(4)

    def print_image_abnt(self, img_name, fig_num, title):
        img_path = os.path.join(EVIDENCIAS_DIR, img_name)
        if os.path.exists(img_path):
            self.ln(2)
            # Título da figura acima (Fonte 10)
            self.set_font("Arial", "", 10)
            self.set_text_color(0, 0, 0)
            self.cell(0, 5, f"Figura {fig_num} – {title}", 0, 1, "C")
            self.ln(1)
            
            # Desenha imagem
            # Largura de 140mm centralizada (largura útil = 160mm)
            self.image(img_path, x=35, w=140)
            self.ln(1)
            
            # Fonte abaixo (Fonte 10)
            self.cell(0, 5, "Fonte: Elaborado pelo autor (2026).", 0, 1, "C")
            self.ln(5)
        else:
            self.print_paragraph(f"[Erro: Imagem {img_name} não encontrada]", "I", 10)

# ----------------------------------------------------
# 3. CONSTRUÇÃO DO CONTEÚDO DO PDF
# ----------------------------------------------------
pdf = PDF_ABNT()

# === CAPA ===
pdf.add_page()
pdf.set_font("Arial", "B", 14)
pdf.cell(0, 15, "CENTRO UNIVERSITÁRIO INTERNACIONAL UNINTER", 0, 1, "C")
pdf.cell(0, 10, "CURSO DE ANÁLISE E DESENVOLVIMENTO DE SISTEMAS", 0, 1, "C")
pdf.ln(50)

pdf.cell(0, 10, "PROJETO MULTIDISCIPLINAR - TRILHA BACK-END", 0, 1, "C")
pdf.set_font("Arial", "B", 18)
pdf.cell(0, 15, "API REST: RAÍZES DO NORDESTE", 0, 1, "C")
pdf.set_font("Arial", "B", 12)
pdf.cell(0, 10, "SISTEMA DE GESTÃO DE ESTOQUE E FIDELIDADE REGIONAL", 0, 1, "C")
pdf.ln(60)

pdf.cell(0, 10, "ESTUDANTE: MATHEUS BESSADO", 0, 1, "C")
pdf.cell(0, 10, "RU: 4712789", 0, 1, "C")
pdf.ln(40)

pdf.cell(0, 10, "FORTALEZA - CE", 0, 1, "C")
pdf.cell(0, 10, "2026", 0, 1, "C")

# === SUMÁRIO (Estático/Clean) ===
pdf.add_page()
pdf.print_title("Sumário")
pdf.set_font("Arial", "", 12)
summary_items = [
    ("1. INTRODUÇÃO E CONTEXTO", "3"),
    ("2. LEVANTAMENTO DE REQUISITOS", "4"),
    ("3. MODELAGEM DO SISTEMA", "5"),
    ("4. ARQUITETURA E TECNOLOGIAS", "7"),
    ("5. FUNCIONALIDADE CRÍTICA", "8"),
    ("6. SEGURANÇA E DIRETRIZES LGPD", "9"),
    ("7. DOCUMENTAÇÃO DOS ENDPOINTS", "10"),
    ("8. PLANO E ANÁLISE DE TESTES", "11"),
    ("9. EVIDÊNCIAS DE EXECUÇÃO", "12"),
    ("10. CONCLUSÃO", "15"),
    ("REFERÊNCIAS BIBLIOGRÁFICAS", "16"),
    ("DECLARAÇÃO DE USO DE IA", "17")
]
for item, page in summary_items:
    dots_count = 80 - len(item) - len(page)
    pdf.cell(0, 8, f"{item} {'.' * dots_count} {page}", 0, 1)

# === 1. INTRODUÇÃO E CONTEXTO ===
pdf.add_page()
pdf.print_title("1. Introdução e Contexto")
pdf.print_paragraph(
    "O mercado regional de lanchonetes e alimentação rápida tem demandado soluções de software cada vez mais ágeis, "
    "integradas e robustas para lidar com a gestão operacional cotidiana. O projeto 'Raízes do Nordeste' surge para "
    "atender a essa demanda específica, modelando o ecossistema de uma rede regional de fast-food com pratos típicos. "
    "A aplicação resolve problemas práticos como a dispersão de pedidos originados de múltiplos canais físicos e digitais "
    "e a necessidade de controle físico transacional de estoque em tempo real."
)
pdf.print_title("1.1 Objetivos do Sistema", 2)
pdf.print_paragraph(
    "O objetivo geral é fornecer uma API REST estável baseada em microsserviços e segurança stateless. Como objetivos específicos, "
    "o sistema implementa a reserva física de insumos no estoque no momento da criação do pedido, integra o callback assíncrono de "
    "gateways de pagamento mockados e gerencia um programa regional de fidelidade baseado em pontos acumulados por compras, tudo "
    "em total conformidade com a Lei Geral de Proteção de Dados (LGPD)."
)

# === 2. LEVANTAMENTO DE REQUISITOS ===
pdf.print_title("2. Levantamento de Requisitos")
pdf.print_title("2.1 Requisitos Funcionais (RF)", 2)
rf_items = [
    ("RF01 - Autenticação de Usuário", "O sistema deve autenticar usuários com diferentes papéis (CLIENTE, COZINHA, ATENDENTE, GERENTE, ADMIN) gerando tokens JWT."),
    ("RF02 - Gestão de Pedidos Multicanal", "O sistema deve registrar pedidos especificando o canal de origem (APP, TOTEM, BALCÃO, PICKUP, WEB)."),
    ("RF03 - Validação Transacional de Estoque", "Ao criar um pedido, o sistema deve reservar de forma física e atômica os itens no estoque da unidade correspondente."),
    ("RF04 - Integração de Pagamento Assíncrona", "O sistema deve expor um endpoint de callback para receber atualizações de status de gateways financeiros."),
    ("RF05 - Programa de Fidelidade", "O sistema deve creditar 1 ponto de fidelidade para cada R$ 1,00 gasto em pedidos aprovados, mediante consentimento do cliente."),
    ("RF06 - Anonimização LGPD", "O sistema deve permitir a exclusão definitiva dos dados pessoais de um usuário (Nome, E-mail, CPF, senha) por motivos de esquecimento.")
]
for rf, desc in rf_items:
    pdf.set_font("Arial", "B", 12)
    pdf.multi_cell(0, 6, rf, new_x="LMARGIN", new_y="NEXT")
    pdf.set_font("Arial", "", 12)
    pdf.multi_cell(0, 6, desc, new_x="LMARGIN", new_y="NEXT")
    pdf.ln(2)

pdf.print_title("2.2 Requisitos Não Funcionais (RNF)", 2)
rnf_items = [
    ("RNF01 - Autenticação Stateless", "A segurança deve ser implementada com Spring Security e tokens JWT assinados digitalmente."),
    ("RNF02 - Banco de Dados Relacional", "Persistência em banco de dados relacional robusto PostgreSQL configurado via Docker."),
    ("RNF03 - Arquitetura de Containers", "A aplicação e o banco devem ser empacotados e executados em ambientes isolados Docker Compose."),
    ("RNF04 - Documentação OpenAPI", "A especificação da API deve ser exposta interativamente via Swagger UI."),
    ("RNF05 - Auditoria de Logs", "Ações críticas (como anonimização e movimentação manual de estoque) devem gerar logs persistentes de auditoria.")
]
for rnf, desc in rnf_items:
    pdf.set_font("Arial", "B", 12)
    pdf.multi_cell(0, 6, rnf, new_x="LMARGIN", new_y="NEXT")
    pdf.set_font("Arial", "", 12)
    pdf.multi_cell(0, 6, desc, new_x="LMARGIN", new_y="NEXT")
    pdf.ln(2)

# === 3. MODELAGEM DO SISTEMA ===
pdf.add_page()
pdf.print_title("3. Modelagem do Sistema")
pdf.print_paragraph(
    "Abaixo são representados os diagramas técnicos do sistema, desenhados para representar a estrutura relacional, operacional e comportamental da API."
)
pdf.print_image_abnt("diagrama_caso_uso.png", 1, "Diagrama de Casos de Uso")
pdf.print_image_abnt("diagrama_der.png", 2, "Modelo Entidade-Relacionamento (DER)")

pdf.add_page()
pdf.print_image_abnt("diagrama_classes.png", 3, "Diagrama de Classes de Domínio")
pdf.print_image_abnt("diagrama_sequencia.png", 4, "Diagrama de Sequência de Pedidos")

pdf.add_page()
pdf.print_image_abnt("diagrama_arquitetura.png", 5, "Diagrama de Arquitetura em Camadas")

# === 4. ARQUITETURA E TECNOLOGIAS ===
pdf.print_title("4. Arquitetura e Tecnologias Utilizadas")
pdf.print_paragraph(
    "O projeto foi estruturado seguindo uma divisão limpa de responsabilidades, garantindo baixo acoplamento e alta coesão:"
)
techs = [
    ("Java 17 (LTS)", "Linguagem de programação moderna, estável e rápida para microsserviços."),
    ("Spring Boot 3.2", "Estrutura ágil para injeção de dependências e configuração simplificada do ecossistema Spring."),
    ("PostgreSQL 15", "SGBD relacional robusto e maduro, ideal para consistência transacional ACID."),
    ("Spring Security + JWT", "Garante segurança stateless baseada em tokens criptografados com algoritmo HMAC-SHA256."),
    ("Docker & Compose", "Padronização do ambiente local de execução da API e banco de dados."),
    ("OpenAPI / Swagger", "Geração e documentação dinâmica dos endpoints REST.")
]
for tech, desc in techs:
    pdf.set_font("Arial", "B", 12)
    pdf.cell(50, 6, tech, 0, 0)
    pdf.set_font("Arial", "", 12)
    pdf.multi_cell(pdf.epw - 50, 6, f"— {desc}")
    pdf.ln(1)

# === 5. FUNCIONALIDADE CRÍTICA ===
pdf.print_title("5. Funcionalidade Crítica: Ciclo do Pedido")
pdf.print_paragraph(
    "A funcionalidade crítica compreende a validação transacional e o fluxo de compra e estoque. Quando um cliente submete "
    "um pedido via POST /pedidos, a aplicação atua sob um escopo transacional (@Transactional): verifica se cada item possui a "
    "quantidade física disponível na respectiva filial. Em caso positivo, o estoque físico é reservado (SAIDA temporária) e o "
    "pedido é registrado com status AGUARDANDO_PAGAMENTO. Se o pagamento mockado for aprovado, o status evolui para PAGAMENTO_CONFIRMADO "
    "e pontos de fidelidade são acumulados. Se o pagamento for recusado, a transação estorna a quantidade ao estoque da unidade."
)

# === 6. SEGURANÇA E DIRETRIZES LGPD ===
pdf.print_title("6. Segurança e Diretrizes LGPD")
pdf.print_paragraph(
    "A API implementa autenticação stateless por JWT. O Spring Security intercepta as requisições, valida a assinatura do token e "
    "concede permissões com base no perfil (ROLE) do ator. Para atender à LGPD, o endpoint PATCH /usuarios/{id}/anonimizar realiza a "
    "limpeza definitiva dos dados pessoais sob o 'Direito ao Esquecimento'. O nome, e-mail, cpf e senhas são substituídos por hashes aleatórios "
    "e a conta é inativada, enquanto os dados financeiros agregados (pedidos) são preservados anonimizados para fins fiscais."
)

# === 7. DOCUMENTAÇÃO DOS ENDPOINTS ===
pdf.add_page()
pdf.print_title("7. Documentação dos Endpoints")
pdf.print_paragraph(
    "Abaixo é apresentado um resumo dos contratos de integração da API REST:"
)
pdf.set_font("Arial", "B", 12)
pdf.cell(0, 6, "POST /auth/login (Público)", 0, 1)
pdf.set_font("Arial", "", 11)
pdf.multi_cell(0, 5, "Finalidade: Autenticar usuários e emitir token JWT.\nRequest Body: { \"email\": \"admin@raizes.com\", \"senha\": \"admin123\" }\nResponse Body: { \"accessToken\": \"ey...\", \"tokenType\": \"Bearer\" }\nStatus Codes: 200 OK, 409 Conflict (Credenciais Inválidas)")
pdf.ln(4)

pdf.set_font("Arial", "B", 12)
pdf.cell(0, 6, "POST /pedidos (Permissão: CLIENTE, ATENDENTE)", 0, 1)
pdf.set_font("Arial", "", 11)
pdf.multi_cell(0, 5, "Finalidade: Criar um pedido e efetuar reserva física.\nRequest Body: { \"canalPedido\": \"APP\", \"unidadeId\": 1, \"itens\": [{ \"produtoId\": 1, \"quantidade\": 2 }] }\nResponse Body: { \"pedidoId\": 2, \"status\": \"AGUARDANDO_PAGAMENTO\", \"total\": 57.8 }\nStatus Codes: 201 Created, 409 Conflict (Estoque Insuficiente)")
pdf.ln(4)

pdf.set_font("Arial", "B", 12)
pdf.cell(0, 6, "POST /pagamentos/callback (Público / Gateway)", 0, 1)
pdf.set_font("Arial", "", 11)
pdf.multi_cell(0, 5, "Finalidade: Atualização de pagamento assíncrono.\nRequest Body: { \"pedidoId\": 2, \"transacaoId\": \"TX-123\", \"status\": \"APROVADO\" }\nResponse: { \"pedidoId\": 2, \"novoStatus\": \"PAGAMENTO_CONFIRMADO\" }\nStatus Codes: 200 OK")
pdf.ln(4)

# === 8. PLANO E ANÁLISE DE TESTES ===
pdf.print_title("8. Plano e Análise de Testes (10 Cenários)")
tests_list = [
    ("C01 - Login válido Admin (Positivo)", "Autentica com credenciais válidas. Retorno: 200 OK com token."),
    ("C02 - Login com credenciais incorretas (Negativo)", "Submete senha inválida. Retorno: 409 Conflict com código CREDENCIAIS_INVALIDAS."),
    ("C03 - Criação de pedido via APP (Positivo)", "Cria pedido com itens em estoque. Retorno: 201 Created."),
    ("C04 - Criação de pedido com estoque zerado (Negativo)", "Submete produto com quantidade indisponível. Retorno: 409 Conflict (ESTOQUE_INSUFICIENTE)."),
    ("C05 - Acesso a endpoint protegido sem token (Negativo)", "Tenta acessar /pedidos sem cabeçalho Authorization. Retorno: 401 Unauthorized."),
    ("C06 - Callback de pagamento aprovado (Positivo)", "Processa transação com sucesso. Retorno: 200 OK com status PAGAMENTO_CONFIRMADO."),
    ("C07 - Atualização de status de preparo (Positivo)", "Cozinheiro avança status do pedido para EM_PREPARO. Retorno: 200 OK."),
    ("C08 - Acesso a rota administrativa por perfil inadequado (Negativo)", "Cliente tenta criar nova unidade. Retorno: 403 Forbidden."),
    ("C09 - Consulta de saldo de fidelidade (Positivo)", "Cliente consulta pontuação acumulada. Retorno: 200 OK."),
    ("C10 - Anonimização de usuário via LGPD (Positivo)", "Admin executa remoção de dados do usuário. Retorno: 200 OK com dados anonimizados.")
]
for c, desc in tests_list:
    pdf.set_font("Arial", "B", 11)
    pdf.multi_cell(0, 5, c, new_x="LMARGIN", new_y="NEXT")
    pdf.set_font("Arial", "", 11)
    pdf.multi_cell(0, 5, desc, new_x="LMARGIN", new_y="NEXT")
    pdf.ln(1)

# === 9. EVIDÊNCIAS DE EXECUÇÃO ===
pdf.add_page()
pdf.print_title("9. Evidências de Execução Real")
pdf.print_paragraph(
    "Nesta seção são incluídos os prints reais de tela capturados das execuções contra o servidor de desenvolvimento local:"
)
pdf.print_image_abnt("figura1_swagger.png", 1, "Documentação da API Raízes do Nordeste via Swagger")
pdf.add_page()
pdf.print_image_abnt("figura2_login.png", 2, "Autenticação de usuário administrador")
pdf.print_image_abnt("figura3_sem_token.png", 3, "Requisição sem autenticação")
pdf.add_page()
pdf.print_image_abnt("figura4_pedido_valido.png", 4, "Criação de pedido com sucesso")
pdf.print_image_abnt("figura5_estoque_insuficiente.png", 5, "Validação de estoque insuficiente")
pdf.add_page()
pdf.print_image_abnt("figura6_callback.png", 6, "Callback de pagamento aprovado")
pdf.print_image_abnt("figura7_status.png", 7, "Atualização de status do pedido")

# === 10. CONCLUSÃO ===
pdf.add_page()
pdf.print_title("10. Conclusão")
pdf.print_paragraph(
    "O desenvolvimento da API REST 'Raízes do Nordeste' permitiu consolidar na prática os conceitos ensinados na trilha "
    "back-end de Análise e Desenvolvimento de Sistemas. A implementação do controle atômico e transacional de estoque, "
    "a validação de payloads com Bean Validation, a segurança robusta do ecossistema Spring Security com tokens JWT "
    "e a conformidade arquitetural de pacotes mostram a viabilidade do projeto para um ambiente real de produção."
)

# === REFERÊNCIAS ===
pdf.print_title("Referências Bibliográficas")
refs = [
    "SPRING FRAMEWORK. Spring Boot Documentation. Disponível em: <https://spring.io/projects/spring-boot>. Acesso em: 26 jun. 2026.",
    "ORACLE. Java Platform, Standard Edition v17 API Specification. Disponível em: <https://docs.oracle.com/en/java/javase/17/>. Acesso em: 26 jun. 2026.",
    "BRASIL. Lei Nº 13.709, de 14 de Agosto de 2018. Lei Geral de Proteção de Dados Pessoais (LGPD). Diário Oficial da União, Brasília, DF, 15 ago. 2018."
]
for ref in refs:
    pdf.set_font("Arial", "", 11)
    pdf.multi_cell(0, 6, ref)
    pdf.ln(2)

# === DECLARAÇÃO DE IA ===
pdf.print_title("Declaração de Uso de Inteligência Artificial")
pdf.print_paragraph(
    "Foi utilizada a ferramenta Gemini/Antigravity como apoio para revisão textual, organização da documentação e auxílio na estruturação dos artefatos do projeto.\n\n"
    "Toda a implementação, modelagem, testes, validações e integração da solução foram realizadas pelo autor.\n\n"
    "A ferramenta não foi utilizada para substituir o desenvolvimento do projeto, mas apenas como suporte documental e consultivo."
)

# Salva o PDF
pdf.output(OUTPUT_PDF)
print(f"Relatório PDF ABNT '{OUTPUT_PDF}' gerado com sucesso!")
