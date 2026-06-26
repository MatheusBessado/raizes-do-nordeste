package com.raizesnordeste.application.service;

import com.raizesnordeste.infrastructure.persistence.repository.FidelidadeRepository;
import com.raizesnordeste.domain.exception.RecursoNaoEncontradoException;
import com.raizesnordeste.infrastructure.persistence.entity.UsuarioEntity;
import com.raizesnordeste.infrastructure.persistence.repository.UsuarioRepository;
import com.raizesnordeste.infrastructure.security.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final AuditoriaService auditoria;

    public Map<String, Object> anonimizar(Long id) {
        System.out.println("Anonimizando usuario ID: " + id);
        
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        String anon = "ANONIMO_" + UUID.randomUUID().toString().substring(0, 8);
        
        usuario.setNome("Anonimizado");
        usuario.setEmail(anon + "@anonimizado.com");
        usuario.setSenhaHash("****");
        usuario.setAtivo(false);
        usuario.setConsentimentoFidelidade(false);

        usuarioRepository.save(usuario);

        // Limpa o saldo de pontos de fidelidade para conformidade com a LGPD ao retirar o consentimento
        fidelidadeRepository.findByUsuarioId(id).ifPresent(fidelidade -> {
            fidelidade.setSaldoPontos(0);
            fidelidadeRepository.save(fidelidade);
        });

        auditoria.registrar("ANONIMIZAR", "usuarios", id, id, "Usuario foi anonimizado LGPD");

        Map<String, Object> resp = new HashMap<>();
        resp.put("mensagem", "Dados anonimizados com sucesso");
        resp.put("id", usuario.getId());

        return resp;
    }
}
