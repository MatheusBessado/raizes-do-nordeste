package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.service.FidelidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fidelidade")
@RequiredArgsConstructor
@Tag(name = "Fidelidade", description = "Programa de pontos e fidelização")
@SecurityRequirement(name = "bearerAuth")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    @GetMapping("/meu-saldo")
    @Operation(summary = "Consultar saldo de pontos do cliente autenticado")
    public ResponseEntity<?> meuSaldo(Authentication auth) {
        return ResponseEntity.ok(fidelidadeService.consultarPorEmail(auth.getName()));
    }
}
