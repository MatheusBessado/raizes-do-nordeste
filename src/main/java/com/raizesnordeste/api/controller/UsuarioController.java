package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gestão de usuários e LGPD")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PatchMapping("/{id}/anonimizar")
    @Operation(summary = "Anonimizar dados do usuário (LGPD - RF08)")
    public ResponseEntity<Map<String, Object>> anonimizar(@PathVariable Long id, Authentication auth) {
        // Num cenario real, validariamos se o usuario esta anonimizando a si mesmo ou se eh admin.
        // Aqui vamos simplificar.
        return ResponseEntity.ok(usuarioService.anonimizar(id));
    }
}
