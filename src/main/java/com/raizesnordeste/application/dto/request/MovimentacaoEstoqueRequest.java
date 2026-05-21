package com.raizesnordeste.application.dto.request;

import jakarta.validation.constraints.*;

public record MovimentacaoEstoqueRequest(
    @NotNull Long produtoId,
    @NotNull @Min(1) Integer quantidade,
    @NotBlank String tipo  // ENTRADA ou SAIDA
) {}
