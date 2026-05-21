package com.raizesnordeste.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs_auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogAuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String acao;

    @Column(nullable = false, length = 60)
    private String recurso;

    private Long recursoId;

    private Long usuarioId;

    @Column(length = 45)
    private String ipOrigem;

    @Column(columnDefinition = "TEXT")
    private String detalhes;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime criadoEm;
}
