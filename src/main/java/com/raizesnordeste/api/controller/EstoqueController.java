package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.MovimentacaoEstoqueRequest;
import com.raizesnordeste.application.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
@Tag(name = "Estoque", description = "Controle de estoque por unidade")
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping("/unidades/{unidadeId}")
    @Operation(summary = "Consultar estoque de uma unidade")
    public ResponseEntity<List<Map<String, Object>>> consultar(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(estoqueService.consultarPorUnidade(unidadeId));
    }

    @PostMapping("/unidades/{unidadeId}/movimentar")
    @Operation(summary = "Registrar entrada ou saída de estoque")
    public ResponseEntity<Map<String, Object>> movimentar(@PathVariable Long unidadeId,
                                                           @Valid @RequestBody MovimentacaoEstoqueRequest req) {
        return ResponseEntity.ok(estoqueService.movimentar(unidadeId, req));
    }
}
