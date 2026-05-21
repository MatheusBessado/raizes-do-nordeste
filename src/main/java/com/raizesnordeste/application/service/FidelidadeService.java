package com.raizesnordeste.application.service;

import com.raizesnordeste.domain.exception.NegocioException;
import com.raizesnordeste.domain.exception.RecursoNaoEncontradoException;
import com.raizesnordeste.infrastructure.persistence.entity.FidelidadeEntity;
import com.raizesnordeste.infrastructure.persistence.entity.UsuarioEntity;
import com.raizesnordeste.infrastructure.persistence.repository.FidelidadeRepository;
import com.raizesnordeste.infrastructure.persistence.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FidelidadeService {

    private final FidelidadeRepository fidelidadeRepository;
    private final UsuarioRepository usuarioRepository;

    public Map<String, Object> consultarPorEmail(String email) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
        return consultarSaldo(usuario.getId());
    }

    public Map<String, Object> consultarSaldo(Long usuarioId) {
        FidelidadeEntity f = fidelidadeRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("FIDELIDADE_NAO_ENCONTRADA",
                        "Usuário não possui programa de fidelidade ativo. " +
                        "Verifique se o consentimento foi fornecido no cadastro."));
        return Map.of(
            "usuarioId", usuarioId,
            "saldoPontos", f.getSaldoPontos(),
            "atualizadoEm", f.getAtualizadoEm() != null ? f.getAtualizadoEm() : f.getCriadoEm()
        );
    }

    @Transactional
    public Map<String, Object> resgatar(Long usuarioId, int pontos) {
        FidelidadeEntity f = fidelidadeRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new NegocioException("FIDELIDADE_NAO_ENCONTRADA",
                        "Programa de fidelidade não encontrado para este usuário."));

        if (f.getSaldoPontos() < pontos) {
            throw new NegocioException("SALDO_INSUFICIENTE",
                    "Saldo insuficiente. Disponível: " + f.getSaldoPontos() + " pontos.");
        }

        f.setSaldoPontos(f.getSaldoPontos() - pontos);
        f.setAtualizadoEm(LocalDateTime.now());
        fidelidadeRepository.save(f);

        return Map.of(
            "usuarioId", usuarioId,
            "pontosResgatados", pontos,
            "saldoRestante", f.getSaldoPontos()
        );
    }
}
