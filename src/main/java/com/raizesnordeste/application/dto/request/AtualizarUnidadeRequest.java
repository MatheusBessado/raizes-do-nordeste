package com.raizesnordeste.application.dto.request;

import jakarta.validation.constraints.Size;

public record AtualizarUnidadeRequest(
    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
    String nome,

    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    String endereco,

    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    String cidade,

    @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres")
    String estado,

    Boolean ativa
) {}
