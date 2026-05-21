package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Relatórios gerenciais da matriz")
@SecurityRequirement(name = "bearerAuth")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/resumo-operacional")
    @Operation(summary = "Obter resumo consolidado para a matriz (RF10)")
    public ResponseEntity<Map<String, Object>> resumoOperacional() {
        return ResponseEntity.ok(relatorioService.gerarResumoOperacional());
    }
}
