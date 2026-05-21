package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.AtualizarStatusPedidoRequest;
import com.raizesnordeste.application.dto.request.CriarPedidoRequest;
import com.raizesnordeste.domain.enums.CanalPedido;
import com.raizesnordeste.domain.enums.StatusPagamento;
import com.raizesnordeste.domain.enums.StatusPedido;
import com.raizesnordeste.domain.exception.NegocioException;
import com.raizesnordeste.domain.exception.RecursoNaoEncontradoException;
import com.raizesnordeste.infrastructure.mock.GatewayPagamentoMock;
import com.raizesnordeste.infrastructure.persistence.entity.*;
import com.raizesnordeste.infrastructure.persistence.repository.*;
import com.raizesnordeste.infrastructure.security.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.raizesnordeste.application.dto.request.CallbackPagamentoRequest;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final PagamentoRepository pagamentoRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final GatewayPagamentoMock gatewayMock;
    private final AuditoriaService auditoria;

    @Transactional
    public Map<String, Object> criarPedido(CriarPedidoRequest req, String emailCliente) {

        UsuarioEntity cliente = usuarioRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));

        UnidadeEntity unidade = unidadeRepository.findById(req.unidadeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade não encontrada."));

        List<ItemPedidoEntity> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CriarPedidoRequest.ItemPedidoRequest itemReq : req.itens()) {
            ProdutoEntity produto = produtoRepository.findById(itemReq.produtoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Produto ID " + itemReq.produtoId() + " não encontrado."));

            if (!produto.getDisponivel()) {
                throw new NegocioException("PRODUTO_INDISPONIVEL",
                        "Produto '" + produto.getNome() + "' não está disponível.");
            }

            EstoqueEntity estoque = estoqueRepository
                    .findByUnidadeIdAndProdutoId(unidade.getId(), produto.getId())
                    .orElseThrow(() -> new NegocioException("ESTOQUE_NAO_ENCONTRADO",
                            "Produto '" + produto.getNome() + "' não disponível nesta unidade."));

            if (estoque.getQuantidade() < itemReq.quantidade()) {
                throw new NegocioException("ESTOQUE_INSUFICIENTE",
                        "Estoque insuficiente para '" + produto.getNome() +
                        "'. Disponível: " + estoque.getQuantidade());
            }

            BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(itemReq.quantidade()));
            total = total.add(subtotal);

            itens.add(ItemPedidoEntity.builder()
                    .produto(produto)
                    .quantidade(itemReq.quantidade())
                    .precoUnitario(produto.getPreco())
                    .subtotal(subtotal)
                    .build());
        }

        PedidoEntity pedido = PedidoEntity.builder()
                .cliente(cliente)
                .unidade(unidade)
                .canalPedido(req.canalPedido())
                .status(StatusPedido.AGUARDANDO_PAGAMENTO)
                .total(total)
                .build();

        pedidoRepository.save(pedido);

        for (int i = 0; i < itens.size(); i++) {
            itens.get(i).setPedido(pedido);
            CriarPedidoRequest.ItemPedidoRequest itemReq = req.itens().get(i);

            int atualizado = estoqueRepository.decrementarEstoque(
                    unidade.getId(), itemReq.produtoId(), itemReq.quantidade());

            if (atualizado == 0) {
                throw new NegocioException("ESTOQUE_INSUFICIENTE", "Falha ao reservar estoque. Tente novamente.");
            }
        }
        pedido.setItens(itens);

        // chama o mock de pagamento
        System.out.println("[DEBUG] chamando gateway mock - pedido: " + pedido.getId() + " valor: " + total);
        GatewayPagamentoMock.RespostaPagamentoMock respostaMock =
                gatewayMock.processar(total, req.formaPagamento());
        System.out.println("[DEBUG] resposta gateway: " + respostaMock.getStatus());

        PagamentoEntity pagamento = PagamentoEntity.builder()
                .pedido(pedido)
                .formaPagamento(req.formaPagamento())
                .status(respostaMock.getStatus())
                .valor(total)
                .transacaoExternaId(respostaMock.getTransacaoId())
                .payloadResposta(respostaMock.getMensagem())
                .processadoEm(LocalDateTime.now())
                .build();

        pagamentoRepository.save(pagamento);

        System.out.println("Salvo no banco o pedido!");

        return montarRespostaPedido(pedido, pagamento, respostaMock.getMensagem());
    }

    @Transactional
    public Map<String, Object> processarCallbackPagamento(CallbackPagamentoRequest req) {
        System.out.println("processando callback");

        PedidoEntity pedido = pedidoRepository.findById(req.pedidoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO) {
            throw new NegocioException("STATUS_INVALIDO", "Pedido não está aguardando pagamento.");
        }

        if (req.status() == StatusPagamento.APROVADO) {
            pedido.setStatus(StatusPedido.PAGAMENTO_CONFIRMADO);
            
            // acumula pontos de fidelidade
            int pontosGanhos = pedido.getTotal().intValue();
            fidelidadeRepository.findByUsuarioId(pedido.getCliente().getId()).ifPresent(fidelidade -> {
                fidelidade.setSaldoPontos(fidelidade.getSaldoPontos() + pontosGanhos);
                fidelidade.setAtualizadoEm(LocalDateTime.now());
                fidelidadeRepository.save(fidelidade);
            });
            
        } else {
            pedido.setStatus(StatusPedido.CANCELADO);
        }

        pedidoRepository.save(pedido);

        auditoria.registrar("CALLBACK_PAGAMENTO", "pedidos", pedido.getId(), null,
                "Status pagamento: " + req.status());

        Map<String, Object> resp = new HashMap<>();
        resp.put("pedidoId", pedido.getId());
        resp.put("novoStatus", pedido.getStatus());
        resp.put("transacaoId", req.transacaoId());
        
        return resp;
    }

    @Transactional
    public Map<String, Object> atualizarStatus(Long pedidoId, AtualizarStatusPedidoRequest req,
                                                String emailUsuario) {
        PedidoEntity pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));

        validarTransicaoStatus(pedido.getStatus(), req.status());

        pedido.setStatus(req.status());
        pedidoRepository.save(pedido);

        auditoria.registrar("ATUALIZAR_STATUS_PEDIDO", "pedidos", pedidoId, null,
                "Novo status: " + req.status());

        System.out.println("atualizou status");

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("pedidoId", pedido.getId());
        resposta.put("status", pedido.getStatus());
        resposta.put("atualizadoEm", pedido.getAtualizadoEm());

        return resposta;
    }

    public Page<PedidoEntity> listar(CanalPedido canal, StatusPedido status, Pageable pageable) {
        return pedidoRepository.filtrar(canal, status, pageable);
    }

    public PedidoEntity buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado."));
    }

    // valida se a transicao de status é valida
    private void validarTransicaoStatus(StatusPedido atual, StatusPedido novo) {
        if (atual == StatusPedido.ENTREGUE || atual == StatusPedido.CANCELADO) {
            throw new NegocioException("TRANSICAO_STATUS_INVALIDA",
                    "Pedido já finalizado, status não pode ser alterado.");
        }

        boolean invalido = false;

        if (atual == StatusPedido.AGUARDANDO_PAGAMENTO) {
            if (novo != StatusPedido.PAGAMENTO_CONFIRMADO && novo != StatusPedido.CANCELADO) {
                invalido = true;
            }
        } else if (atual == StatusPedido.PAGAMENTO_CONFIRMADO) {
            if (novo != StatusPedido.EM_PREPARO && novo != StatusPedido.CANCELADO) {
                invalido = true;
            }
        } else if (atual == StatusPedido.EM_PREPARO) {
            if (novo != StatusPedido.PRONTO) {
                invalido = true;
            }
        } else if (atual == StatusPedido.PRONTO) {
            if (novo != StatusPedido.ENTREGUE) {
                invalido = true;
            }
        }

        if (invalido) {
            throw new NegocioException("TRANSICAO_STATUS_INVALIDA",
                    "Transição de " + atual + " para " + novo + " não é permitida.");
        }
    }

    private Map<String, Object> montarRespostaPedido(PedidoEntity pedido,
                                                     PagamentoEntity pagamento,
                                                     String mensagemPagamento) {
        
        List<Map<String, Object>> itensResp = new ArrayList<>();
        for (ItemPedidoEntity i : pedido.getItens()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("produtoId", i.getProduto().getId());
            itemMap.put("nomeProduto", i.getProduto().getNome());
            itemMap.put("quantidade", i.getQuantidade());
            itemMap.put("precoUnitario", i.getPrecoUnitario());
            itemMap.put("subtotal", i.getSubtotal());
            itensResp.add(itemMap);
        }

        Map<String, Object> pagamentoMap = new HashMap<>();
        pagamentoMap.put("status", pagamento.getStatus());
        pagamentoMap.put("transacaoId", pagamento.getTransacaoExternaId());
        pagamentoMap.put("mensagem", mensagemPagamento);

        Map<String, Object> response = new HashMap<>();
        response.put("pedidoId", pedido.getId());
        response.put("canalPedido", pedido.getCanalPedido());
        response.put("status", pedido.getStatus());
        response.put("total", pedido.getTotal());
        response.put("itens", itensResp);
        response.put("pagamento", pagamentoMap);
        response.put("criadoEm", pedido.getCriadoEm());

        return response;
    }
}
