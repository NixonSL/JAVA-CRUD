package org.example.projeto.repository;

import org.example.projeto.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    // Buscar carrinho por userId (agora String, não User)
    Optional<Cart> findByUserId(String userId);

    // Verificar se usuário já tem carrinho
    boolean existsByUserId(String userId);

    // Buscar carrinho com produtos carregados
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.produtos WHERE c.userId = :userId")
    Optional<Cart> findCartWithProdutosByUserId(@Param("userId") String userId);

    // Verificar se um produto específico está no carrinho
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Cart c JOIN c.produtos p " +
            "WHERE c.userId = :userId AND p.id = :produtoId")
    boolean existsProdutoInCart(@Param("userId") String userId, @Param("produtoId") Long produtoId);

    // Contar quantos produtos no carrinho
    @Query("SELECT COUNT(p) FROM Cart c JOIN c.produtos p WHERE c.userId = :userId")
    int countProdutosInCart(@Param("userId") String userId);
}