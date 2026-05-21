package com.raizesnordeste.infrastructure.persistence.repository;

import com.raizesnordeste.infrastructure.persistence.entity.LogAuditoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogAuditoriaRepository extends JpaRepository<LogAuditoriaEntity, Long> {
}
