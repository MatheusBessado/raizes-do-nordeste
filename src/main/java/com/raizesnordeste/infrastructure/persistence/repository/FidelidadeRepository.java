package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.infrastructure.persistence.entity.FidelidadeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FidelidadeRepository extends JpaRepository<FidelidadeEntity, Long> {
    Optional<FidelidadeEntity> findByUsuarioId(Long usuarioId);
}
