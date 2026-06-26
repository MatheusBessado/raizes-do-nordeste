package com.raizesnordeste.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raizesnordeste.application.dto.request.CriarPedidoRequest;
import com.raizesnordeste.domain.enums.CanalPedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "cliente@raizes.com", roles = "CLIENTE")
    void criarPedidoComSucesso() throws Exception {
        var item = new CriarPedidoRequest.ItemPedidoRequest(1L, 2);
        var req = new CriarPedidoRequest(CanalPedido.APP, 1L, List.of(item), "PIX");

        mockMvc.perform(post("/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").exists())
                .andExpect(jsonPath("$.total").value(57.80)) // 28.90 * 2 = 57.80
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
    }

    @Test
    void criarPedidoSemAutenticacaoRetornaUnauthorized() throws Exception {
        var item = new CriarPedidoRequest.ItemPedidoRequest(1L, 2);
        var req = new CriarPedidoRequest(CanalPedido.APP, 1L, List.of(item), "PIX");

        mockMvc.perform(post("/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
