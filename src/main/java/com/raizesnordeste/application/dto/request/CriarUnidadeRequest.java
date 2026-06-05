package com.raizesnordeste.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarUnidadeRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 120, message = "Nome deve ter no máximo 120 caracteres")
    String nome,

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    String endereco,

    @NotBlank(message = "Cidade é obrigatória")
    @Size(max = 50, message = "Cidade deve ter no máximo 50 caracteres")
    String cidade,

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2, message = "Estado deve ter exatamente 2 caracteres (ex: CE)")
    String estado
) {}
