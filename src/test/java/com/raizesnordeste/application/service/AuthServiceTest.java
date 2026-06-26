package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.CadastroUsuarioRequest;
import com.raizesnordeste.application.dto.request.LoginRequest;
import com.raizesnordeste.domain.enums.PerfilUsuario;
import com.raizesnordeste.domain.exception.NegocioException;
import com.raizesnordeste.infrastructure.persistence.entity.FidelidadeEntity;
import com.raizesnordeste.infrastructure.persistence.entity.UsuarioEntity;
import com.raizesnordeste.infrastructure.persistence.repository.FidelidadeRepository;
import com.raizesnordeste.infrastructure.persistence.repository.UsuarioRepository;
import com.raizesnordeste.infrastructure.security.AuditoriaService;
import com.raizesnordeste.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private FidelidadeRepository fidelidadeRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditoriaService auditoria;

    @InjectMocks
    private AuthService authService;

    private UsuarioEntity usuario;

    @BeforeEach
    void setUp() {
        usuario = UsuarioEntity.builder()
                .id(1L)
                .nome("Maria Silva")
                .email("maria@email.com")
                .senhaHash("hash_senha")
                .perfil(PerfilUsuario.CLIENTE)
                .ativo(true)
                .consentimentoFidelidade(true)
                .build();
    }

    @Test
    void loginComSucesso() {
        LoginRequest req = new LoginRequest("maria@email.com", "senha123");
        when(usuarioRepository.findByEmail(req.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(req.senha(), usuario.getSenhaHash())).thenReturn(true);
        when(jwtService.gerarToken(usuario.getEmail(), usuario.getPerfil().name())).thenReturn("token_jwt");

        Map<String, Object> resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("token_jwt", resp.get("accessToken"));
        verify(auditoria, times(1)).registrar(eq("LOGIN"), eq("usuarios"), any(), any(), any());
    }

    @Test
    void loginComSenhaInvalidaLancaNegocioException() {
        LoginRequest req = new LoginRequest("maria@email.com", "senha_errada");
        when(usuarioRepository.findByEmail(req.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(req.senha(), usuario.getSenhaHash())).thenReturn(false);

        assertThrows(NegocioException.class, () -> authService.login(req));
        verify(jwtService, never()).gerarToken(any(), any());
    }

    @Test
    void cadastrarComSucessoCriaFidelidadeSeConsentido() {
        CadastroUsuarioRequest req = new CadastroUsuarioRequest(
                "Novo User", "novo@email.com", "senha123", PerfilUsuario.CLIENTE, true
        );
        when(usuarioRepository.existsByEmail(req.email())).thenReturn(false);
        when(passwordEncoder.encode(req.senha())).thenReturn("hash_nova");

        Map<String, Object> resp = authService.cadastrar(req);

        assertNotNull(resp);
        assertEquals("novo@email.com", resp.get("email"));
        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));
        verify(fidelidadeRepository, times(1)).save(any(FidelidadeEntity.class));
    }
}
