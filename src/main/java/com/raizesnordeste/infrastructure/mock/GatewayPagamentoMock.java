package com.raizesnordeste.infrastructure.mock;

import com.raizesnordeste.domain.enums.StatusPagamento;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mock do gateway de pagamento externo.
 * Em producao isso seria uma chamada HTTP para um servico real (ex: PagSeguro, Mercado Pago).
 *
 * Regra usada no mock: pedidos acima de R$500 sao recusados automaticamente
 * para conseguir testar o fluxo de pagamento negado.
 */
@Service
public class GatewayPagamentoMock {

    public RespostaPagamentoMock processar(BigDecimal valor, String formaPagamento) {
        // simulacao: valores altos sao recusados
        boolean aprovado = valor.compareTo(new BigDecimal("500.00")) <= 0;

        return RespostaPagamentoMock.builder()
                .transacaoId(UUID.randomUUID().toString())
                .status(aprovado ? StatusPagamento.APROVADO : StatusPagamento.RECUSADO)
                .mensagem(aprovado ? "Pagamento aprovado com sucesso." : "Limite excedido. Pagamento recusado.")
                .valor(valor)
                .formaPagamento(formaPagamento)
                .build();
    }

    @lombok.Builder
    @lombok.Getter
    public static class RespostaPagamentoMock {
        private String transacaoId;
        private StatusPagamento status;
        private String mensagem;
        private BigDecimal valor;
        private String formaPagamento;
    }
}
