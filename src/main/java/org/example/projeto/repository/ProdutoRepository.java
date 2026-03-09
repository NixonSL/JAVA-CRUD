package org.example.projeto.repository;

import org.example.projeto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository // Indica que esta interface é um componente Spring (bean) da camada de persistência
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // JpaRepository já fornece métodos prontos:
    // - save()    -> salvar/atualizar
    // - findAll() -> listar todos
    // - findById()-> buscar por ID
    // - delete()  -> deletar
    // - count()   -> contar registros

    // 1. Busca produtos por categoria (exata)
    // Ex: GET /api/produtos/categoria/Eletrônicos
    List<Produto> findByCategoria(String categoria);

    // 2. Busca produtos por nome (ignora maiúsculas/minúsculas, contém trecho)
    // Ex: GET /api/produtos/buscar/nome?nome=note
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // 3. Busca produtos com preço entre valor mínimo e máximo
    // Ex: GET /api/produtos/buscar/preco?min=100&max=1000
    List<Produto> findByPrecoBetween(BigDecimal precoMin, BigDecimal precoMax);

    // 4. Busca produtos com quantidade menor que o valor informado (estoque baixo)
    // Ex: GET /api/produtos/buscar/estoque-baixo?quantidade=5
    List<Produto> findByQuantidadeLessThan(Integer quantidade);

    // 5. Busca por categoria E preço máximo (usando JPQL)
    // Ex: GET /api/produtos/buscar/categoria-preco?categoria=Eletrônicos&precoMax=2000
    @Query("SELECT p FROM Produto p WHERE p.categoria = :categoria AND p.preco <= :precoMax")
    List<Produto> buscarPorCategoriaEPrecoMax(
            @Param("categoria") String categoria,
            @Param("precoMax") BigDecimal precoMax
    );

    // 6. Lista todos ordenados por preço (crescente)
    // Ex: GET /api/produtos/ordenados/preco
    List<Produto> findAllByOrderByPrecoAsc();

    // 7. Lista todos ordenados por nome (crescente)
    // Ex: GET /api/produtos/ordenados/nome
    List<Produto> findAllByOrderByNomeAsc();
}