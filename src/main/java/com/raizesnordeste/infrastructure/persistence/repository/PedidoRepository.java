package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.domain.enums.CanalPedido;
import com.raizesnordeste.domain.enums.StatusPedido;
import com.raizesnordeste.infrastructure.persistence.entity.PedidoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PedidoRepository extends JpaRepository<PedidoEntity, Long> {

    Page<PedidoEntity> findByClienteId(Long clienteId, Pageable pageable);

    Page<PedidoEntity> findByCanalPedido(CanalPedido canal, Pageable pageable);

    Page<PedidoEntity> findByStatus(StatusPedido status, Pageable pageable);

    @Query("SELECT p FROM PedidoEntity p WHERE " +
           "(:canal IS NULL OR p.canalPedido = :canal) AND " +
           "(:status IS NULL OR p.status = :status)")
    Page<PedidoEntity> filtrar(CanalPedido canal, StatusPedido status, Pageable pageable);
}
