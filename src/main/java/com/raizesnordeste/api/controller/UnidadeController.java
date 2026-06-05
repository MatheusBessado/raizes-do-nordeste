package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.CriarUnidadeRequest;
import com.raizesnordeste.application.dto.request.AtualizarUnidadeRequest;
import com.raizesnordeste.application.service.UnidadeService;
import com.raizesnordeste.infrastructure.persistence.entity.UnidadeEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "Gestão das unidades da rede")
@SecurityRequirement(name = "bearerAuth")
public class UnidadeController {

    private final UnidadeService unidadeService;

    @GetMapping
    @Operation(summary = "Listar todas as unidades ativas")
    public ResponseEntity<List<UnidadeEntity>> listar() {
        return ResponseEntity.ok(unidadeService.listarAtivas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar unidade por ID")
    public ResponseEntity<UnidadeEntity> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar nova unidade (ADMIN)")
    public ResponseEntity<UnidadeEntity> criar(@Valid @RequestBody CriarUnidadeRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(unidadeService.criar(req));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar unidade por ID (ADMIN)")
    public ResponseEntity<UnidadeEntity> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody AtualizarUnidadeRequest req) {
        return ResponseEntity.ok(unidadeService.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar/Excluir unidade por ID (ADMIN)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        unidadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
