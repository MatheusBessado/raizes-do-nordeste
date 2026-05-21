package com.raizesnordeste.application.dto.request;

import com.raizesnordeste.domain.enums.StatusPagamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CallbackPagamentoRequest(
    @NotNull Long pedidoId,
    @NotBlank String transacaoId,
    @NotNull StatusPagamento status,
    String mensagem
) {}
