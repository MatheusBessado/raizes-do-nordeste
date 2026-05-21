package com.raizesnordeste.application.dto.request;

import com.raizesnordeste.domain.enums.CanalPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CriarPedidoRequest(
    @NotNull(message = "Canal do pedido é obrigatório")
    CanalPedido canalPedido,

    @NotNull(message = "Unidade é obrigatória")
    Long unidadeId,

    @NotEmpty(message = "O pedido deve ter ao menos um item")
    @Valid
    List<ItemPedidoRequest> itens,

    @NotBlank(message = "Forma de pagamento é obrigatória")
    String formaPagamento
) {
    public record ItemPedidoRequest(
        @NotNull(message = "Produto é obrigatório")
        Long produtoId,

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser no mínimo 1")
        Integer quantidade
    ) {}
}
