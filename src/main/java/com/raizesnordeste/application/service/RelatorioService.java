package com.raizesnordeste.application.service;

import com.raizesnordeste.domain.enums.StatusPedido;
import com.raizesnordeste.infrastructure.persistence.entity.PedidoEntity;
import com.raizesnordeste.infrastructure.persistence.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final PedidoRepository pedidoRepository;

    public Map<String, Object> gerarResumoOperacional() {
        System.out.println("Gerando relatorio...");
        
        List<PedidoEntity> todosPedidos = pedidoRepository.findAll();
        
        int totalPedidos = todosPedidos.size();
        int pedidosConcluidos = 0;
        int pedidosCancelados = 0;
        BigDecimal faturamentoTotal = BigDecimal.ZERO;

        // Fazer a logica de contagem manualmente usando for para ser mais basico
        for (PedidoEntity p : todosPedidos) {
            if (p.getStatus() == StatusPedido.ENTREGUE) {
                pedidosConcluidos++;
                faturamentoTotal = faturamentoTotal.add(p.getTotal());
            } else if (p.getStatus() == StatusPedido.CANCELADO) {
                pedidosCancelados++;
            }
        }

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("totalPedidosRegistrados", totalPedidos);
        relatorio.put("pedidosEntregues", pedidosConcluidos);
        relatorio.put("pedidosCancelados", pedidosCancelados);
        relatorio.put("faturamentoTotalEstimado", faturamentoTotal);

        return relatorio;
    }
}
