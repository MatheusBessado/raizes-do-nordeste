package com.raizesnordeste.application.dto.request;

import com.raizesnordeste.domain.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusPedidoRequest(
    @NotNull(message = "Status é obrigatório")
    StatusPedido status
) {}
