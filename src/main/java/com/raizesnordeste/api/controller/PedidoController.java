package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.AtualizarStatusPedidoRequest;
import com.raizesnordeste.application.dto.request.CriarPedidoRequest;
import com.raizesnordeste.application.service.PedidoService;
import com.raizesnordeste.domain.enums.CanalPedido;
import com.raizesnordeste.domain.enums.StatusPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Criação e gestão de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @Operation(summary = "Criar novo pedido (fluxo crítico: pedido → pagamento mock → status)")
    public ResponseEntity<Map<String, Object>> criar(@Valid @RequestBody CriarPedidoRequest req,
                                                      Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.criarPedido(req, auth.getName()));
    }

    @GetMapping
    @Operation(summary = "Listar pedidos com filtro por canal e status")
    public ResponseEntity<Page<?>> listar(
            @RequestParam(required = false) CanalPedido canalPedido,
            @RequestParam(required = false) StatusPedido status,
            @PageableDefault(size = 10, sort = "criadoEm") Pageable pageable) {
        return ResponseEntity.ok(pedidoService.listar(canalPedido, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pedido (cozinha/atendente)")
    public ResponseEntity<Map<String, Object>> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusPedidoRequest req,
            Authentication auth) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, req, auth.getName()));
    }
}
