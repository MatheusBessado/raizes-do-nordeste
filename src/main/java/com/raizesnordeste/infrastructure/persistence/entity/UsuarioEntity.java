package com.raizesnordeste.infrastructure.persistence.entity;

import com.raizesnordeste.domain.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PerfilUsuario perfil;

    @Column(nullable = false)
    private Boolean ativo = true;

    // LGPD: consentimento para programa de fidelização
    @Column(nullable = false)
    private Boolean consentimentoFidelidade = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
