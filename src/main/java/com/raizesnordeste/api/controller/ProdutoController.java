package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.service.ProdutoService;
import com.raizesnordeste.infrastructure.persistence.entity.ProdutoEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Catálogo de produtos da rede")
@SecurityRequirement(name = "bearerAuth")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @Operation(summary = "Listar produtos disponíveis (com paginação e filtro por categoria)")
    public ResponseEntity<Page<ProdutoEntity>> listar(
            @RequestParam(required = false) String categoria,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(produtoService.listar(categoria, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProdutoEntity> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo produto (ADMIN/GERENTE)")
    public ResponseEntity<ProdutoEntity> criar(@RequestBody Map<String, Object> body) {
        ProdutoEntity produto = produtoService.criar(
            (String) body.get("nome"),
            (String) body.get("descricao"),
            new BigDecimal(body.get("preco").toString()),
            (String) body.get("categoria")
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto (ADMIN/GERENTE)")
    public ResponseEntity<ProdutoEntity> atualizar(@PathVariable Long id,
                                                    @RequestBody Map<String, Object> body) {
        BigDecimal preco = body.get("preco") != null
                ? new BigDecimal(body.get("preco").toString()) : null;
        return ResponseEntity.ok(produtoService.atualizar(id,
            (String) body.get("nome"),
            (String) body.get("descricao"),
            preco,
            (String) body.get("categoria"),
            body.get("disponivel") != null ? (Boolean) body.get("disponivel") : null
        ));
    }
}
