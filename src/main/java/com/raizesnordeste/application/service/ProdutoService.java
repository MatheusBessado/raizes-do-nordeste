package com.raizesnordeste.application.service;

import com.raizesnordeste.domain.exception.RecursoNaoEncontradoException;
import com.raizesnordeste.infrastructure.persistence.entity.ProdutoEntity;
import com.raizesnordeste.infrastructure.persistence.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public Page<ProdutoEntity> listar(String categoria, Pageable pageable) {
        if (categoria != null && !categoria.isBlank()) {
            return produtoRepository.findByCategoriaIgnoreCase(categoria, pageable);
        }
        return produtoRepository.findByDisponivelTrue(pageable);
    }

    public ProdutoEntity buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));
    }

    public ProdutoEntity criar(String nome, String descricao, BigDecimal preco, String categoria) {
        return produtoRepository.save(ProdutoEntity.builder()
                .nome(nome).descricao(descricao).preco(preco)
                .categoria(categoria).disponivel(true).build());
    }

    public ProdutoEntity atualizar(Long id, String nome, String descricao, BigDecimal preco,
                                   String categoria, Boolean disponivel) {
        ProdutoEntity p = buscarPorId(id);
        if (nome != null) p.setNome(nome);
        if (descricao != null) p.setDescricao(descricao);
        if (preco != null) p.setPreco(preco);
        if (categoria != null) p.setCategoria(categoria);
        if (disponivel != null) p.setDisponivel(disponivel);
        return produtoRepository.save(p);
    }
}
