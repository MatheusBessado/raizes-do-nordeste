package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.infrastructure.persistence.entity.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<PagamentoEntity, Long> {
    Optional<PagamentoEntity> findByPedidoId(Long pedidoId);
}
