package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.CriarUnidadeRequest;
import com.raizesnordeste.application.dto.request.AtualizarUnidadeRequest;
import com.raizesnordeste.domain.exception.RecursoNaoEncontradoException;
import com.raizesnordeste.infrastructure.persistence.entity.UnidadeEntity;
import com.raizesnordeste.infrastructure.persistence.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    public List<UnidadeEntity> listarAtivas() {
        return unidadeRepository.findByAtivaTrue();
    }

    public UnidadeEntity buscarPorId(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade não encontrada."));
    }

    @Transactional
    public UnidadeEntity criar(CriarUnidadeRequest req) {
        UnidadeEntity unidade = UnidadeEntity.builder()
                .nome(req.nome())
                .endereco(req.endereco())
                .cidade(req.cidade())
                .estado(req.estado())
                .ativa(true)
                .build();
        return unidadeRepository.save(unidade);
    }

    @Transactional
    public UnidadeEntity atualizar(Long id, AtualizarUnidadeRequest req) {
        UnidadeEntity unidade = buscarPorId(id);
        if (req.nome() != null) unidade.setNome(req.nome());
        if (req.endereco() != null) unidade.setEndereco(req.endereco());
        if (req.cidade() != null) unidade.setCidade(req.cidade());
        if (req.estado() != null) unidade.setEstado(req.estado());
        if (req.ativa() != null) unidade.setAtiva(req.ativa());
        return unidadeRepository.save(unidade);
    }

    @Transactional
    public void deletar(Long id) {
        UnidadeEntity unidade = buscarPorId(id);
        // Soft delete para preservar integridade de dados e histórico de pedidos
        unidade.setAtiva(false);
        unidadeRepository.save(unidade);
    }
}
