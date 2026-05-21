package com.raizesnordeste.application.dto.request;

import com.raizesnordeste.domain.enums.PerfilUsuario;
import jakarta.validation.constraints.*;

public record CadastroUsuarioRequest(
    @NotBlank(message = "Nome obrigatório")
    @Size(min = 2, max = 120)
    String nome,

    @NotBlank(message = "E-mail obrigatório")
    @Email(message = "E-mail inválido")
    String email,

    @NotBlank(message = "Senha obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    String senha,

    @NotNull(message = "Perfil obrigatório")
    PerfilUsuario perfil,

    Boolean consentimentoFidelidade
) {}
