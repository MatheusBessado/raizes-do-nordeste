package com.raizesnordeste.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CriarProdutoRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
    String nome,

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    String descricao,

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    BigDecimal preco,

    @NotBlank(message = "Categoria é obrigatória")
    @Size(max = 60, message = "Categoria deve ter no máximo 60 caracteres")
    String categoria
) {}
