const fs = require('fs');
const path = require('path');

const BASE_URL = 'http://localhost:8080';

async function runTests() {
  console.log('Iniciando testes da API "Raízes do Nordeste"...');
  const results = {};

  try {
    // ----------------------------------------------------
    // T01: Login válido (Admin) -> Status 200
    // ----------------------------------------------------
    console.log('\nExecutando T01: Login válido (Admin)...');
    const loginAdminRes = await fetch(`${BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'admin@raizes.com', senha: 'admin123' })
    });
    
    results.T01 = {
      url: `${BASE_URL}/auth/login`,
      method: 'POST',
      status: loginAdminRes.status,
      headers: Object.fromEntries(loginAdminRes.headers.entries()),
      body: await loginAdminRes.json()
    };
    console.log(`T01 concluído com status ${results.T01.status}`);
    const tokenAdmin = results.T01.body.accessToken;

    // Login do Cliente para gerar token do cliente
    console.log('\nGerando Token de Cliente para testes de pedidos...');
    const loginClienteRes = await fetch(`${BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'cliente@raizes.com', senha: 'cliente123' })
    });
    const clienteLoginData = await loginClienteRes.json();
    const tokenCliente = clienteLoginData.accessToken;
    console.log('Token de cliente obtido com sucesso.');

    // ----------------------------------------------------
    // T02: Erro 401 sem token (Acesso indevido a /pedidos) -> Status 401
    // ----------------------------------------------------
    console.log('\nExecutando T02: Erro 401 sem token...');
    const noTokenRes = await fetch(`${BASE_URL}/pedidos`, {
      method: 'GET'
    });
    
    let noTokenBody;
    try {
      noTokenBody = await noTokenRes.json();
    } catch {
      noTokenBody = await noTokenRes.text();
    }

    results.T02 = {
      url: `${BASE_URL}/pedidos`,
      method: 'GET',
      status: noTokenRes.status,
      headers: Object.fromEntries(noTokenRes.headers.entries()),
      body: noTokenBody
    };
    console.log(`T02 concluído com status ${results.T02.status}`);

    // ----------------------------------------------------
    // T05: Criação de Pedido com canalPedido APP -> Status 201
    // ----------------------------------------------------
    console.log('\nExecutando T05: Criação de Pedido com canalPedido APP...');
    const createPedidoRes = await fetch(`${BASE_URL}/pedidos`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${tokenCliente}`
      },
      body: JSON.stringify({
        canalPedido: 'APP',
        unidadeId: 1,
        itens: [{ produtoId: 1, quantidade: 2 }],
        formaPagamento: 'MOCK'
      })
    });

    results.T05 = {
      url: `${BASE_URL}/pedidos`,
      method: 'POST',
      status: createPedidoRes.status,
      headers: Object.fromEntries(createPedidoRes.headers.entries()),
      body: await createPedidoRes.json()
    };
    console.log(`T05 concluído com status ${results.T05.status}`);
    const pedidoId = results.T05.body.pedidoId;

    // ----------------------------------------------------
    // T07: Erro 409 Estoque Insuficiente -> Status 409
    // ----------------------------------------------------
    console.log('\nExecutando T07: Erro 409 Estoque Insuficiente...');
    // Produto 2 (Baião de Dois) tem estoque = 0 na Unidade 2 (Recife) no DataSeeder.java
    const insufficientStockRes = await fetch(`${BASE_URL}/pedidos`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${tokenCliente}`
      },
      body: JSON.stringify({
        canalPedido: 'WEB',
        unidadeId: 2,
        itens: [{ produtoId: 2, quantity: 1, quantidade: 1 }], // handle both structures if needed
        formaPagamento: 'MOCK'
      })
    });

    results.T07 = {
      url: `${BASE_URL}/pedidos`,
      method: 'POST',
      status: insufficientStockRes.status,
      headers: Object.fromEntries(insufficientStockRes.headers.entries()),
      body: await insufficientStockRes.json()
    };
    console.log(`T07 concluído com status ${results.T07.status}`);

    // ----------------------------------------------------
    // T08: Callback Pagamento Aprovado -> Status 200
    // ----------------------------------------------------
    console.log(`\nExecutando T08: Callback Pagamento Aprovado para o Pedido ID ${pedidoId}...`);
    const callbackRes = await fetch(`${BASE_URL}/pagamentos/callback`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        pedidoId: pedidoId,
        transacaoId: 'TX-4712789-MOCK',
        status: 'APROVADO',
        mensagem: 'Pagamento efetuado com sucesso via Gateway de Teste'
      })
    });

    results.T08 = {
      url: `${BASE_URL}/pagamentos/callback`,
      method: 'POST',
      status: callbackRes.status,
      headers: Object.fromEntries(callbackRes.headers.entries()),
      body: await callbackRes.json()
    };
    console.log(`T08 concluído com status ${results.T08.status}`);

    // ----------------------------------------------------
    // T10: Atualizar status pedido para EM_PREPARO -> Status 200
    // ----------------------------------------------------
    console.log(`\nExecutando T10: Atualizar status do pedido ${pedidoId} para EM_PREPARO...`);
    const updateStatusRes = await fetch(`${BASE_URL}/pedidos/${pedidoId}/status`, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${tokenAdmin}`
      },
      body: JSON.stringify({
        status: 'EM_PREPARO'
      })
    });

    results.T10 = {
      url: `${BASE_URL}/pedidos/${pedidoId}/status`,
      method: 'PATCH',
      status: updateStatusRes.status,
      headers: Object.fromEntries(updateStatusRes.headers.entries()),
      body: await updateStatusRes.json()
    };
    console.log(`T10 concluído com status ${results.T10.status}`);

    // Salvar resultados
    const outputPath = path.join(__dirname, 'test_results.json');
    fs.writeFileSync(outputPath, JSON.stringify(results, null, 2), 'utf-8');
    console.log(`\nResultados salvos com sucesso em: ${outputPath}`);

  } catch (error) {
    console.error('Erro ao executar os testes:', error);
  }
}

runTests();
