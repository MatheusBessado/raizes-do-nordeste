package com.raizesnordeste.infrastructure.security;

import com.raizesnordeste.infrastructure.persistence.entity.LogAuditoriaEntity;
import com.raizesnordeste.infrastructure.persistence.repository.LogAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final LogAuditoriaRepository logRepository;

    @Async
    public void registrar(String acao, String recurso, Long recursoId, Long usuarioId, String detalhes) {
        LogAuditoriaEntity log = LogAuditoriaEntity.builder()
                .acao(acao)
                .recurso(recurso)
                .recursoId(recursoId)
                .usuarioId(usuarioId)
                .detalhes(detalhes)
                .build();
        logRepository.save(log);
    }
}
