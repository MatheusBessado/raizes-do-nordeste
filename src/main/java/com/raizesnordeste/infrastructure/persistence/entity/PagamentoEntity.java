package com.raizesnordeste.infrastructure.persistence.entity;

import com.raizesnordeste.domain.enums.StatusPagamento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private PedidoEntity pedido;

    @Column(nullable = false, length = 20)
    private String formaPagamento;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    // Payload retornado pelo mock de pagamento
    @Column(columnDefinition = "TEXT")
    private String payloadResposta;

    // ID externo retornado pelo gateway mock
    @Column(length = 100)
    private String transacaoExternaId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime processadoEm;
}
