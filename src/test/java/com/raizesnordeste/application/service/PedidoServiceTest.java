package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.CriarPedidoRequest;
import com.raizesnordeste.domain.enums.CanalPedido;
import com.raizesnordeste.domain.enums.StatusPagamento;
import com.raizesnordeste.domain.enums.StatusPedido;
import com.raizesnordeste.domain.exception.NegocioException;
import com.raizesnordeste.infrastructure.mock.GatewayPagamentoMock;
import com.raizesnordeste.infrastructure.persistence.entity.*;
import com.raizesnordeste.infrastructure.persistence.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private UnidadeRepository unidadeRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private PagamentoRepository pagamentoRepository;
    @Mock
    private FidelidadeRepository fidelidadeRepository;
    @Mock
    private GatewayPagamentoMock gatewayMock;
    @Mock
    private com.raizesnordeste.infrastructure.security.AuditoriaService auditoria;

    @InjectMocks
    private PedidoService pedidoService;

    private UsuarioEntity cliente;
    private UnidadeEntity unidade;
    private ProdutoEntity produto;
    private EstoqueEntity estoque;

    @BeforeEach
    void setUp() {
        cliente = UsuarioEntity.builder()
                .id(1L)
                .nome("Maria")
                .email("maria@email.com")
                .consentimentoFidelidade(true)
                .build();

        unidade = UnidadeEntity.builder()
                .id(1L)
                .nome("Fortaleza")
                .ativa(true)
                .build();

        produto = ProdutoEntity.builder()
                .id(1L)
                .nome("Tapioca")
                .preco(BigDecimal.valueOf(20.00))
                .disponivel(true)
                .build();

        estoque = EstoqueEntity.builder()
                .id(1L)
                .unidade(unidade)
                .produto(produto)
                .quantidade(10)
                .build();
    }

    @Test
    void criarPedidoComSucesso() {
        var itemReq = new CriarPedidoRequest.ItemPedidoRequest(1L, 2);
        var req = new CriarPedidoRequest(1L, CanalPedido.APP, List.of(itemReq), "CARTAO");

        when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(cliente));
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(estoqueRepository.findByUnidadeIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(estoque));
        when(estoqueRepository.decrementarEstoque(1L, 1L, 2)).thenReturn(1);

        GatewayPagamentoMock.RespostaPagamentoMock respostaMock = GatewayPagamentoMock.RespostaPagamentoMock.builder()
                .status(StatusPagamento.APROVADO)
                .transacaoId("tx-123")
                .mensagem("Sucesso")
                .valor(BigDecimal.valueOf(40.00))
                .build();

        when(gatewayMock.processar(any(), any())).thenReturn(respostaMock);

        var response = pedidoService.criarPedido(req, "maria@email.com");

        assertNotNull(response);
        assertEquals(StatusPedido.AGUARDANDO_PAGAMENTO, response.get("status"));
        assertEquals(BigDecimal.valueOf(40.00), response.get("total"));
        verify(pedidoRepository, times(1)).save(any(PedidoEntity.class));
        verify(pagamentoRepository, times(1)).save(any(PagamentoEntity.class));
    }

    @Test
    void criarPedidoEstoqueInsuficienteLancaExcecao() {
        var itemReq = new CriarPedidoRequest.ItemPedidoRequest(1L, 50); // mais que 10
        var req = new CriarPedidoRequest(1L, CanalPedido.APP, List.of(itemReq), "CARTAO");

        when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(Optional.of(cliente));
        when(unidadeRepository.findById(1L)).thenReturn(Optional.of(unidade));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(estoqueRepository.findByUnidadeIdAndProdutoId(1L, 1L)).thenReturn(Optional.of(estoque));

        assertThrows(NegocioException.class, () -> pedidoService.criarPedido(req, "maria@email.com"));
        verify(pedidoRepository, never()).save(any());
    }
}
