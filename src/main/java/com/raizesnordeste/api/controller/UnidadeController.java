package com.raizesnordeste.api.controller;

import com.raizesnordeste.domain.exception.RecursoNaoEncontradoException;
import com.raizesnordeste.infrastructure.persistence.repository.UnidadeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "Gestão das unidades da rede")
@SecurityRequirement(name = "bearerAuth")
public class UnidadeController {

    private final UnidadeRepository unidadeRepository;

    @GetMapping
    @Operation(summary = "Listar todas as unidades ativas")
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(unidadeRepository.findByAtivaTrue());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar unidade por ID")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(
            unidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade não encontrada."))
        );
    }
}
