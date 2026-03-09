package org.example.projeto.service;

import org.example.projeto.entity.Produto;
import org.example.projeto.exception.RecursoNaoEncontradoException;
import org.example.projeto.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.validation.Valid;
import java.math.BigDecimal; // NOVO: necessário para preço
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    // =============================================
    // MÉTODOS QUE JÁ EXISTIAM (NÃO MEXEMOS)
    // =============================================
    public boolean existePorId(Long id) {
        return produtoRepository.existsById(id);
    }

    public Produto salvar(@Valid Produto produtoParaSalvar) {
        return produtoRepository.save(produtoParaSalvar);
    }

    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public void deletar(Long id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
        } else {
            throw new RecursoNaoEncontradoException("Produto não encontrado com id: " + id);
        }
    }

    public Produto atualizar(Long id, @Valid Produto produtoAtualizado) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produto.setNome(produtoAtualizado.getNome());
                    produto.setDescricao(produtoAtualizado.getDescricao());
                    produto.setPreco(produtoAtualizado.getPreco());           // NOVO
                    produto.setQuantidade(produtoAtualizado.getQuantidade()); // NOVO
                    produto.setCategoria(produtoAtualizado.getCategoria());   // NOVO
                    return produtoRepository.save(produto);
                })
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com id: " + id));
    }

    // =============================================
    // NOVOS MÉTODOS CUSTOMIZADOS (O QUE FOI ADICIONADO)
    // =============================================

    // 1. Buscar por categoria
    public List<Produto> buscarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    // 2. Buscar por nome (contém, ignorando maiúsculas/minúsculas)
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // 3. Buscar por faixa de preço
    public List<Produto> buscarPorPrecoEntre(BigDecimal min, BigDecimal max) {
        return produtoRepository.findByPrecoBetween(min, max);
    }

    // 4. Buscar produtos com estoque baixo (quantidade < valor informado)
    public List<Produto> buscarEstoqueBaixo(Integer quantidade) {
        return produtoRepository.findByQuantidadeLessThan(quantidade);
    }

    // 5. Buscar por categoria e preço máximo (combinação)
    public List<Produto> buscarPorCategoriaEPrecoMax(String categoria, BigDecimal precoMax) {
        return produtoRepository.buscarPorCategoriaEPrecoMax(categoria, precoMax);
    }

    // 6. Listar ordenados por preço (crescente)
    public List<Produto> listarOrdenadosPorPreco() {
        return produtoRepository.findAllByOrderByPrecoAsc();
    }

    // 7. Listar ordenados por nome (crescente)
    public List<Produto> listarOrdenadosPorNome() {
        return produtoRepository.findAllByOrderByNomeAsc();
    }
}