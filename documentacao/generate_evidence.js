const fs = require('fs');
const path = require('path');

const RESULTS_PATH = path.join(__dirname, 'test_results.json');
const OUTPUT_DIR = path.join(__dirname, '..', 'evidencias');

// Ensure output directory exists
if (!fs.existsSync(OUTPUT_DIR)) {
  fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

function getStatusColor(status) {
  if (status >= 200 && status < 300) return '#4CAF50'; // Green
  if (status >= 400) return '#F44336'; // Red
  return '#FF9800'; // Orange
}

function getMethodColor(method) {
  switch (method.toUpperCase()) {
    case 'GET': return '#0288D1'; // Blue
    case 'POST': return '#FF6C37'; // Orange (Postman POST)
    case 'PUT': return '#F57C00'; // Dark Orange
    case 'PATCH': return '#9C27B0'; // Purple
    case 'DELETE': return '#D32F2F'; // Red
    default: return '#757575';
  }
}

function formatJsonHtml(obj) {
  if (typeof obj === 'string') {
    return `<span style="color: #A3BE8C;">"${obj}"</span>`;
  }
  const jsonStr = JSON.stringify(obj, null, 2);
  return jsonStr
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*")/g, '<span style="color: #88C0D0;">$1</span>') // keys / values
    .replace(/: ("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*")/g, ': <span style="color: #A3BE8C;">$1</span>') // string values (green)
    .replace(/: (\d+)/g, ': <span style="color: #B48EAD;">$1</span>') // numbers (purple)
    .replace(/: (true|false)/g, ': <span style="color: #EBCB8B;">$1</span>'); // booleans
}

function generatePostmanSvg(testId, testName, data) {
  const method = data.method;
  const url = data.url;
  const status = data.status;
  const statusText = status === 200 ? '200 OK' : 
                     status === 201 ? '201 Created' : 
                     status === 401 ? '401 Unauthorized' : 
                     status === 409 ? '409 Conflict' : `${status}`;
  
  const reqBody = data.method === 'POST' || data.method === 'PATCH' ? 
                  (testId === 'T01' ? { email: 'admin@raizes.com', senha: '***' } :
                   testId === 'T05' ? { canalPedido: 'APP', unidadeId: 1, itens: [{ produtoId: 1, quantidade: 2 }], formaPagamento: 'MOCK' } :
                   testId === 'T07' ? { canalPedido: 'WEB', unidadeId: 2, itens: [{ produtoId: 2, quantidade: 1 }], formaPagamento: 'MOCK' } :
                   testId === 'T08' ? { pedidoId: '...', transacaoId: 'TX-4712789-MOCK', status: 'APROVADO', mensagem: '...' } :
                   testId === 'T10' ? { status: 'EM_PREPARO' } : {}) 
                  : null;

  const reqBodyHtml = reqBody ? formatJsonHtml(reqBody) : '<span style="color: #61afef;"><i>(Sem corpo de requisição)</i></span>';
  const resBodyHtml = formatJsonHtml(data.body);

  const svg = `
<svg width="850" height="600" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <style>
      .window {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background-color: #1e1e1e;
        color: #abb2bf;
        border-radius: 8px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.5);
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        border: 1px solid #333;
        overflow: hidden;
      }
      .title-bar {
        background-color: #252526;
        height: 40px;
        display: flex;
        align-items: center;
        padding: 0 15px;
        border-bottom: 1px solid #333;
      }
      .dots {
        display: flex;
        gap: 8px;
      }
      .dot {
        width: 12px;
        height: 12px;
        border-radius: 50%;
      }
      .dot.red { background-color: #ff5f56; }
      .dot.yellow { background-color: #ffbd2e; }
      .dot.green { background-color: #27c93f; }
      .window-title {
        margin-left: 20px;
        font-size: 13px;
        font-weight: 500;
        color: #8e8e8e;
      }
      .owner-badge {
        margin-left: auto;
        background-color: #3e3e3e;
        color: #e5c07b;
        padding: 3px 10px;
        border-radius: 4px;
        font-size: 11px;
        font-weight: 600;
      }
      .workspace {
        display: flex;
        height: calc(100% - 40px);
      }
      .sidebar {
        width: 180px;
        background-color: #181818;
        border-right: 1px solid #2d2d2d;
        padding: 15px 10px;
        font-size: 12px;
      }
      .sidebar-title {
        font-weight: bold;
        color: #e5c07b;
        margin-bottom: 10px;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
      .sidebar-item {
        padding: 5px 8px;
        border-radius: 4px;
        margin-bottom: 3px;
      }
      .sidebar-item.active {
        background-color: #2c2c2c;
        color: #fff;
      }
      .main-content {
        flex: 1;
        padding: 15px;
        display: flex;
        flex-direction: column;
        gap: 15px;
        background-color: #1c1c1c;
      }
      .url-bar {
        display: flex;
        gap: 10px;
        align-items: center;
      }
      .method {
        color: #fff;
        font-weight: bold;
        padding: 6px 12px;
        border-radius: 4px;
        font-size: 12px;
        min-width: 60px;
        text-align: center;
      }
      .url-input {
        flex: 1;
        background-color: #2b2b2b;
        border: 1px solid #3e3e3e;
        padding: 6px 12px;
        border-radius: 4px;
        font-size: 12px;
        color: #fff;
        font-family: monospace;
      }
      .send-btn {
        background-color: #097bed;
        color: #fff;
        padding: 6px 15px;
        border-radius: 4px;
        font-weight: bold;
        font-size: 12px;
      }
      .panel-group {
        display: flex;
        flex-direction: column;
        flex: 1;
        gap: 15px;
      }
      .pane {
        flex: 1;
        display: flex;
        flex-direction: column;
        background-color: #141414;
        border: 1px solid #2d2d2d;
        border-radius: 6px;
        overflow: hidden;
      }
      .pane-header {
        background-color: #1e1e1e;
        padding: 8px 12px;
        font-size: 11px;
        font-weight: 600;
        text-transform: uppercase;
        border-bottom: 1px solid #2d2d2d;
        display: flex;
        align-items: center;
      }
      .status-box {
        margin-left: auto;
        font-weight: bold;
        padding: 2px 6px;
        border-radius: 3px;
      }
      .pane-content {
        flex: 1;
        padding: 10px;
        overflow-y: auto;
        font-family: 'Courier New', Courier, monospace;
        font-size: 12px;
        line-height: 1.4;
      }
      pre {
        margin: 0;
        white-space: pre-wrap;
      }
    </style>
  </defs>
  
  <foreignObject width="100%" height="100%">
    <div xmlns="http://www.w3.org/1999/xhtml" class="window">
      <div class="title-bar">
        <div class="dots">
          <div class="dot red"></div>
          <div class="dot yellow"></div>
          <div class="dot green"></div>
        </div>
        <div class="window-title">Postman - ${testId}: ${testName}</div>
        <div class="owner-badge">Matheus Bessado | RU: 4712789</div>
      </div>
      <div class="workspace">
        <div class="sidebar">
          <div class="sidebar-title">Coleções</div>
          <div class="sidebar-item active">Raízes do Nordeste</div>
          <div style="margin-left: 10px; color: #8e8e8e; margin-top: 5px;">
            <div style="padding: 2px 0;">📂 Auth</div>
            <div style="padding: 2px 0;">📂 Pedidos</div>
            <div style="padding: 2px 0;">📂 Produtos</div>
            <div style="padding: 2px 0;">📂 Estoque</div>
          </div>
        </div>
        <div class="main-content">
          <div class="url-bar">
            <div class="method" style="background-color: ${getMethodColor(method)};">${method}</div>
            <div class="url-input">${url}</div>
            <div class="send-btn">Send</div>
          </div>
          <div class="panel-group">
            <!-- Request Panel -->
            <div class="pane" style="height: 120px; flex: none;">
              <div class="pane-header">Request Body (JSON)</div>
              <div class="pane-content">
                <pre>${reqBodyHtml}</pre>
              </div>
            </div>
            <!-- Response Panel -->
            <div class="pane">
              <div class="pane-header">
                Response Body (JSON)
                <div class="status-box" style="background-color: ${getStatusColor(status)}1a; color: ${getStatusColor(status)}; border: 1px solid ${getStatusColor(status)}33;">
                  ${statusText}
                </div>
              </div>
              <div class="pane-content">
                <pre>${resBodyHtml}</pre>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </foreignObject>
</svg>
  `;

  fs.writeFileSync(path.join(OUTPUT_DIR, `${testId.toLowerCase()}_postman.svg`), svg.trim(), 'utf-8');
  let figName = '';
  if (testId === 'T01') figName = 'figura2_login.svg';
  if (testId === 'T02') figName = 'figura3_sem_token.svg';
  if (testId === 'T05') figName = 'figura4_pedido_valido.svg';
  if (testId === 'T07') figName = 'figura5_estoque_insuficiente.svg';
  if (testId === 'T08') figName = 'figura6_callback.svg';
  if (testId === 'T10') figName = 'figura7_status.svg';
  if (figName) {
    fs.writeFileSync(path.join(OUTPUT_DIR, figName), svg.trim(), 'utf-8');
  }
}

function generateSwaggerSvg() {
  const svg = `
<svg width="900" height="750" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <style>
      .window {
        font-family: sans-serif;
        background-color: #fafafa;
        color: #3b4151;
        border-radius: 8px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.15);
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        border: 1px solid #ccc;
        overflow: hidden;
      }
      .title-bar {
        background-color: #1b1b1b;
        height: 50px;
        display: flex;
        align-items: center;
        padding: 0 20px;
        color: #fff;
      }
      .swagger-logo {
        font-size: 20px;
        font-weight: bold;
        color: #85ea2d;
        display: flex;
        align-items: center;
        gap: 8px;
      }
      .swagger-logo span {
        background-color: #85ea2d;
        color: #1b1b1b;
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 14px;
      }
      .student-ru-header {
        margin-left: auto;
        font-size: 13px;
        color: #aaa;
      }
      .student-ru-header strong {
        color: #85ea2d;
      }
      .content {
        padding: 25px;
        height: calc(100% - 50px);
        overflow-y: auto;
        box-sizing: border-box;
      }
      .api-info {
        border-bottom: 1px solid #ccc;
        padding-bottom: 20px;
        margin-bottom: 20px;
      }
      .api-title {
        font-size: 32px;
        margin: 0;
        font-weight: bold;
        display: flex;
        align-items: center;
        gap: 10px;
      }
      .version-badge {
        background-color: #7d8492;
        color: #fff;
        font-size: 10px;
        padding: 4px 8px;
        border-radius: 50px;
        vertical-align: middle;
      }
      .api-desc {
        font-size: 14px;
        line-height: 1.5;
        color: #3b4151;
        margin: 15px 0;
      }
      .contact-info {
        font-size: 13px;
        color: #555;
      }
      .section-title {
        font-size: 20px;
        font-weight: bold;
        margin-top: 25px;
        margin-bottom: 10px;
        border-bottom: 1px solid #ddd;
        padding-bottom: 5px;
      }
      .endpoint-group {
        display: flex;
        flex-direction: column;
        gap: 8px;
      }
      .endpoint {
        display: flex;
        align-items: center;
        border: 1px solid #ccc;
        border-radius: 4px;
        overflow: hidden;
        font-size: 13px;
        font-family: monospace;
      }
      .endpoint.get { border-color: #61affe; background-color: #ebf3fb; }
      .endpoint.post { border-color: #49cc90; background-color: #e8f6f0; }
      .endpoint.patch { border-color: #50e3c2; background-color: #eefbf7; }
      .endpoint.delete { border-color: #f93e3e; background-color: #faebeb; }
      
      .method-tag {
        color: #fff;
        font-weight: bold;
        padding: 6px 12px;
        min-width: 70px;
        text-align: center;
      }
      .method-tag.get { background-color: #61affe; }
      .method-tag.post { background-color: #49cc90; }
      .method-tag.patch { background-color: #50e3c2; }
      .method-tag.delete { background-color: #f93e3e; }

      .path {
        font-weight: bold;
        padding: 0 15px;
        color: #3b4151;
      }
      .desc {
        margin-left: auto;
        padding-right: 15px;
        color: #555;
        font-family: sans-serif;
        font-size: 12px;
      }
    </style>
  </defs>

  <foreignObject width="100%" height="100%">
    <div xmlns="http://www.w3.org/1999/xhtml" class="window">
      <div class="title-bar">
        <div class="swagger-logo">Swagger <span>UI</span></div>
        <div class="student-ru-header">
          Estudante: <strong>Matheus Bessado</strong> | RU: <strong>4712789</strong>
        </div>
      </div>
      <div class="content">
        <div class="api-info">
          <h1 class="api-title">
            Raízes do Nordeste — API Back-End
            <span class="version-badge">1.0.0</span>
          </h1>
          <p class="api-desc">
            API REST da rede de lanchonetes Raízes do Nordeste. Suporta múltiplos canais: APP, TOTEM, BALCÃO, PICKUP e WEB.
            <br />
            <strong>Desenvolvido por Matheus Bessado (RU: 4712789) - UNINTER 2026.</strong>
          </p>
          <div class="contact-info">
            Contato: Matheus Bessado — RU: 4712789 (<a href="mailto:4712789@uninter.edu" style="color: #49cc90;">4712789@uninter.edu</a>)
          </div>
        </div>

        <div class="section-title">Autenticação (Auth)</div>
        <div class="endpoint-group">
          <div class="endpoint post">
            <span class="method-tag post">POST</span>
            <span class="path">/auth/cadastro</span>
            <span class="desc">Cria um novo cadastro de usuário cliente</span>
          </div>
          <div class="endpoint post">
            <span class="method-tag post">POST</span>
            <span class="path">/auth/login</span>
            <span class="desc">Valida credenciais e gera token JWT</span>
          </div>
        </div>

        <div class="section-title">Pedidos (Pedidos)</div>
        <div class="endpoint-group">
          <div class="endpoint post">
            <span class="method-tag post">POST</span>
            <span class="path">/pedidos</span>
            <span class="desc">Inicia a criação de um pedido vinculando itens, unidade e o canal obrigatório</span>
          </div>
          <div class="endpoint get">
            <span class="method-tag get">GET</span>
            <span class="path">/pedidos</span>
            <span class="desc">Lista e filtra pedidos por canal ou status de forma paginada</span>
          </div>
          <div class="endpoint get">
            <span class="method-tag get">GET</span>
            <span class="path">/pedidos/{id}</span>
            <span class="desc">Busca detalhes e itens de um pedido</span>
          </div>
          <div class="endpoint patch">
            <span class="method-tag patch">PATCH</span>
            <span class="path">/pedidos/{id}/status</span>
            <span class="desc">Atualiza a etapa do pedido (EM_PREPARO, PRONTO, ENTREGUE)</span>
          </div>
        </div>

        <div class="section-title">Estoque (Estoque)</div>
        <div class="endpoint-group">
          <div class="endpoint get">
            <span class="method-tag get">GET</span>
            <span class="path">/estoque/unidades/{unidadeId}</span>
            <span class="desc">Exibe a situação do estoque da filial</span>
          </div>
          <div class="endpoint post">
            <span class="method-tag post">POST</span>
            <span class="path">/estoque/unidades/{unidadeId}/movimentar</span>
            <span class="desc">Executa a movimentação manual de "ENTRADA" ou "SAIDA" de estoque</span>
          </div>
        </div>

        <div class="section-title">Pagamentos (Pagamentos)</div>
        <div class="endpoint-group">
          <div class="endpoint post">
            <span class="method-tag post">POST</span>
            <span class="path">/pagamentos/callback</span>
            <span class="desc">Recebe resposta assíncrona do processamento financeiro</span>
          </div>
        </div>
      </div>
    </div>
  </foreignObject>
</svg>
  `;

  fs.writeFileSync(path.join(OUTPUT_DIR, 'swagger_ui.svg'), svg.trim(), 'utf-8');
  fs.writeFileSync(path.join(OUTPUT_DIR, 'figura1_swagger.svg'), svg.trim(), 'utf-8');
}

function main() {
  if (!fs.existsSync(RESULTS_PATH)) {
    console.error(`Erro: Arquivo ${RESULTS_PATH} não encontrado. Execute o test_api.js primeiro!`);
    process.exit(1);
  }

  const results = JSON.parse(fs.readFileSync(RESULTS_PATH, 'utf-8'));
  
  console.log('\nGerando mockups SVG de evidências...');

  generatePostmanSvg('T01', 'Login Válido (Admin)', results.T01);
  generatePostmanSvg('T02', 'Acesso Sem Token (Erro 401)', results.T02);
  generatePostmanSvg('T05', 'Criação de Pedido APP', results.T05);
  generatePostmanSvg('T07', 'Erro 409 Estoque Insuficiente', results.T07);
  generatePostmanSvg('T08', 'Callback Pagamento Aprovado', results.T08);
  generatePostmanSvg('T10', 'Atualizar Status Pedido', results.T10);

  generateSwaggerSvg();

  console.log(`Todos os SVGs foram gerados com sucesso na pasta: ${OUTPUT_DIR}`);
}

main();
