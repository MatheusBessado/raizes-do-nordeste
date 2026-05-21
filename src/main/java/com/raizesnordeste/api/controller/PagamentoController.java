package com.raizesnordeste.api.controller;

import com.raizesnordeste.application.dto.request.CallbackPagamentoRequest;
import com.raizesnordeste.application.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Endpoints para integração com gateway de pagamento")
public class PagamentoController {

    private final PedidoService pedidoService;

    @PostMapping("/callback")
    @Operation(summary = "Receber atualização de pagamento do gateway externo (RF06)")
    public ResponseEntity<Map<String, Object>> callback(
            @Valid @RequestBody CallbackPagamentoRequest req) {
        
        System.out.println("Recebendo callback do gateway de pagamento para o pedido: " + req.pedidoId());
        Map<String, Object> resultado = pedidoService.processarCallbackPagamento(req);
        
        return ResponseEntity.ok(resultado);
    }
}
