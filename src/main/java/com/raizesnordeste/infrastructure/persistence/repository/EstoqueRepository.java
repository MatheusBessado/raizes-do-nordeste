package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.infrastructure.persistence.entity.EstoqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<EstoqueEntity, Long> {

    Optional<EstoqueEntity> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);

    List<EstoqueEntity> findByUnidadeId(Long unidadeId);

    @Modifying
    @Query("UPDATE EstoqueEntity e SET e.quantidade = e.quantidade - :qtd " +
           "WHERE e.unidade.id = :unidadeId AND e.produto.id = :produtoId AND e.quantidade >= :qtd")
    int decrementarEstoque(Long unidadeId, Long produtoId, int qtd);
}
