package org.example.projeto.service;

import org.example.projeto.entity.Produto;
import org.example.projeto.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Indica que esta classe é um componente Spring da camada de serviço (regras de negócio)
public class ProdutoService {

    public boolean existePorId(Long id) {
        return produtoRepository.existsById(id);
    }

    @Autowired // Injeta automaticamente uma instância do ProdutoRepository gerenciada pelo Spring
    private ProdutoRepository produtoRepository;

    // Salva um produto no banco de dados
    // O metodo save() é fornecido pelo JpaRepository
    public Produto salvar(Produto produtoParaSalvar) {
        return produtoRepository.save(produtoParaSalvar);
    }

    // Retorna todos os produtos do banco de dados
    // O metodo findAll() é fornecido pelo JpaRepository
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    //Faz busca de produtos por ID
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    //deleta produto pela ID
    public void deletar(Long id) {
        if (produtoRepository.existsById(id)) produtoRepository.deleteById(id);
        else {
            throw new RuntimeException("Produto não encontrado com id: " + id);
        }
    }

    // PUT Atualiza os campos do produto
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        return produtoRepository.findById(id)
                .map(produto -> {
                    produto.setNome(produtoAtualizado.getNome());
                    produto.setDescricao(produtoAtualizado.getDescricao());
                    return produtoRepository.save(produto);
                })
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
    }

}