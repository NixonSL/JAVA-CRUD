package org.example.projeto.repository;

import org.example.projeto.entity.Cart;
import org.example.projeto.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    // Buscar carrinho pelo usuário
    Optional<Cart> findByUser(User user);

    // Buscar carrinho pelo ID do usuário
    Optional<Cart> findByUserId(String userId);

    // Verificar se usuário já tem carrinho
    boolean existsByUser(User user);

    // Buscar carrinho com os produtos carregados (evita LazyInitializationException)
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.produtos WHERE c.user = :user")
    Optional<Cart> findCartWithProdutosByUser(@Param("user") User user);

    // Buscar carrinho com produtos pelo ID do usuário
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.produtos WHERE c.user.id = :userId")
    Optional<Cart> findCartWithProdutosByUserId(@Param("userId") String userId);

    // Verificar se um produto específico está no carrinho do usuário
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Cart c JOIN c.produtos p " +
            "WHERE c.user = :user AND p.id = :produtoId")
    boolean existsProdutoInCart(@Param("user") User user, @Param("produtoId") Long produtoId);

    // Contar quantos produtos no carrinho
    @Query("SELECT COUNT(p) FROM Cart c JOIN c.produtos p WHERE c.user = :user")
    int countProdutosInCart(@Param("user") User user);

    // Remover todos os produtos do carrinho (limpar)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cart_produtos WHERE cart_id = :cartId", nativeQuery = true)
    void clearCart(@Param("cartId") String cartId);

    // Remover um produto específico do carrinho
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cart_produtos WHERE cart_id = :cartId AND produto_id = :produtoId", nativeQuery = true)
    int removeProdutoFromCart(@Param("cartId") String cartId, @Param("produtoId") Long produtoId);

    // Adicionar produto ao carrinho (evita duplicidade)
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO cart_produtos (cart_id, produto_id) VALUES (:cartId, :produtoId) " +
            "ON CONFLICT DO NOTHING", nativeQuery = true)
    void addProdutoToCart(@Param("cartId") String cartId, @Param("produtoId") Long produtoId);
}