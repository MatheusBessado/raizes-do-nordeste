package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.infrastructure.persistence.entity.ProdutoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, Long> {
    Page<ProdutoEntity> findByDisponivelTrue(Pageable pageable);
    Page<ProdutoEntity> findByCategoriaIgnoreCase(String categoria, Pageable pageable);
}
