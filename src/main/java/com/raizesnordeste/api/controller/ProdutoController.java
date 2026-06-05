package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.AtualizarProdutoRequest;
import com.raizesnordeste.application.dto.request.CriarProdutoRequest;
import com.raizesnordeste.application.service.ProdutoService;
import com.raizesnordeste.infrastructure.persistence.entity.ProdutoEntity;
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
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ProdutoEntity> criar(@Valid @RequestBody CriarProdutoRequest req) {
        ProdutoEntity produto = produtoService.criar(
            req.nome(),
            req.descricao(),
            req.preco(),
            req.categoria()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto (ADMIN/GERENTE)")
    public ResponseEntity<ProdutoEntity> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody AtualizarProdutoRequest req) {
        return ResponseEntity.ok(produtoService.atualizar(
            id,
            req.nome(),
            req.descricao(),
            req.preco(),
            req.categoria(),
            req.disponivel()
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir/Inativar produto (ADMIN/GERENTE)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
