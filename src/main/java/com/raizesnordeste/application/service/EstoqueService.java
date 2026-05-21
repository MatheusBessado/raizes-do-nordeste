package com.raizesnordeste.application.service;

import com.raizesnordeste.application.dto.request.MovimentacaoEstoqueRequest;
import com.raizesnordeste.domain.exception.NegocioException;
import com.raizesnordeste.domain.exception.RecursoNaoEncontradoException;
import com.raizesnordeste.infrastructure.persistence.entity.EstoqueEntity;
import com.raizesnordeste.infrastructure.persistence.repository.EstoqueRepository;
import com.raizesnordeste.infrastructure.persistence.repository.ProdutoRepository;
import com.raizesnordeste.infrastructure.persistence.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional
    public Map<String, Object> movimentar(Long unidadeId, MovimentacaoEstoqueRequest req) {
        var unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade não encontrada."));

        var produto = produtoRepository.findById(req.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado."));

        EstoqueEntity estoque = estoqueRepository
                .findByUnidadeIdAndProdutoId(unidadeId, req.produtoId())
                .orElseGet(() -> EstoqueEntity.builder()
                        .unidade(unidade)
                        .produto(produto)
                        .quantidade(0)
                        .build());

        // TODO: talvez valha a pena criar um enum pra ENTRADA/SAIDA no futuro
        if (req.tipo().equalsIgnoreCase("ENTRADA")) {
            estoque.setQuantidade(estoque.getQuantidade() + req.quantidade());
        } else if (req.tipo().equalsIgnoreCase("SAIDA")) {
            if (estoque.getQuantidade() < req.quantidade()) {
                throw new NegocioException("ESTOQUE_INSUFICIENTE",
                        "Quantidade insuficiente. Disponível: " + estoque.getQuantidade());
            }
            estoque.setQuantidade(estoque.getQuantidade() - req.quantidade());
        } else {
            throw new NegocioException("TIPO_INVALIDO", "Tipo deve ser ENTRADA ou SAIDA.");
        }

        System.out.println("salvando estoque no banco de dados");
        estoqueRepository.save(estoque);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("unidadeId", unidadeId);
        resposta.put("produtoId", produto.getId());
        resposta.put("nomeProduto", produto.getNome());
        resposta.put("quantidadeAtual", estoque.getQuantidade());
        resposta.put("tipo", req.tipo());

        return resposta;
    }

    public List<Map<String, Object>> consultarPorUnidade(Long unidadeId) {
        unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Unidade não encontrada."));

        List<EstoqueEntity> estoques = estoqueRepository.findByUnidadeId(unidadeId);
        List<Map<String, Object>> resposta = new ArrayList<>();

        for (EstoqueEntity e : estoques) {
            Map<String, Object> item = new HashMap<>();
            item.put("produtoId", e.getProduto().getId());
            item.put("nomeProduto", e.getProduto().getNome());
            item.put("quantidade", e.getQuantidade());
            item.put("atualizadoEm", e.getAtualizadoEm());
            resposta.add(item);
        }

        return resposta;
    }
}
