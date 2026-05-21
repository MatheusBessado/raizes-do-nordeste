package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.CadastroUsuarioRequest;
import com.raizesnordeste.application.dto.request.LoginRequest;
import com.raizesnordeste.domain.exception.NegocioException;
import com.raizesnordeste.infrastructure.persistence.entity.FidelidadeEntity;
import com.raizesnordeste.infrastructure.persistence.entity.UsuarioEntity;
import com.raizesnordeste.infrastructure.persistence.repository.FidelidadeRepository;
import com.raizesnordeste.infrastructure.persistence.repository.UsuarioRepository;
import com.raizesnordeste.infrastructure.security.AuditoriaService;
import com.raizesnordeste.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoria;

    public Map<String, Object> login(LoginRequest req) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(req.email())
                .orElseThrow(() -> new NegocioException("CREDENCIAIS_INVALIDAS", "E-mail ou senha inválidos."));

        if (!usuario.getAtivo()) {
            throw new NegocioException("USUARIO_INATIVO", "Usuário desativado. Contate o suporte.");
        }

        if (!passwordEncoder.matches(req.senha(), usuario.getSenhaHash())) {
            throw new NegocioException("CREDENCIAIS_INVALIDAS", "E-mail ou senha inválidos.");
        }

        String token = jwtService.gerarToken(usuario.getEmail(), usuario.getPerfil().name());

        // registra login no log de auditoria (LGPD)
        auditoria.registrar("LOGIN", "usuarios", usuario.getId(), usuario.getId(), "Login realizado");

        System.out.println("Logando usuario: " + req.email());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", usuario.getId());
        userMap.put("nome", usuario.getNome());
        userMap.put("email", usuario.getEmail());
        userMap.put("perfil", usuario.getPerfil());

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", token);
        response.put("tokenType", "Bearer");
        response.put("expiresIn", 86400);
        response.put("user", userMap);

        return response;
    }

    @Transactional
    public Map<String, Object> cadastrar(CadastroUsuarioRequest req) {
        if (usuarioRepository.existsByEmail(req.email())) {
            throw new NegocioException("EMAIL_JA_CADASTRADO", "Este e-mail já está em uso.");
        }

        UsuarioEntity usuario = UsuarioEntity.builder()
                .nome(req.nome())
                .email(req.email())
                .senhaHash(passwordEncoder.encode(req.senha()))
                .perfil(req.perfil())
                .ativo(true)
                .consentimentoFidelidade(req.consentimentoFidelidade() != null && req.consentimentoFidelidade())
                .build();

        usuarioRepository.save(usuario);

        // cria saldo de fidelidade se o usuario deu consentimento (LGPD)
        if (Boolean.TRUE.equals(usuario.getConsentimentoFidelidade())) {
            fidelidadeRepository.save(FidelidadeEntity.builder()
                    .usuario(usuario)
                    .saldoPontos(0)
                    .build());
        }

        auditoria.registrar("CADASTRO", "usuarios", usuario.getId(), usuario.getId(), "Novo usuário cadastrado");

        System.out.println("cadastro efetuado com sucesso");

        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("nome", usuario.getNome());
        response.put("email", usuario.getEmail());
        response.put("perfil", usuario.getPerfil());

        return response;
    }
}
