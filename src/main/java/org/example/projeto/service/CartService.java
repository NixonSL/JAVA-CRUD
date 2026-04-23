package org.example.projeto.service;

import org.example.projeto.dto.CartItemDTO;
import org.example.projeto.dto.CartResponseDTO;
import org.example.projeto.entity.Cart;
import org.example.projeto.entity.Produto;
import org.example.projeto.entity.User;
import org.example.projeto.repository.CartRepository;
import org.example.projeto.repository.ProdutoRepository;
import org.example.projeto.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. Criar carrinho para um novo usuário
    @Transactional
    public Cart createCartForUser(User user) {
        // Verificar se usuário já tem carrinho
        if (cartRepository.existsByUser(user)) {
            return cartRepository.findByUser(user).get();
        }

        // Buscar o usuário completo do banco para garantir que o ID está carregado
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + user.getId()));

        // Criar novo carrinho com o usuário gerenciado
        Cart cart = new Cart();
        cart.setUser(managedUser);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        cart.setProdutos(new ArrayList<>());

        return cartRepository.save(cart);
    }

    // 2. Buscar carrinho do usuário (com produtos)
    @Transactional
    public CartResponseDTO getCartByUser(User user) {
        // Buscar usuário gerenciado
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Buscar carrinho com produtos carregados
        Cart cart = cartRepository.findCartWithProdutosByUser(managedUser)
                .orElseGet(() -> createCartForUser(managedUser));

        // Converter para DTO
        return convertToResponseDTO(cart);
    }
    // 3. Adicionar produto ao carrinho
    @Transactional
    public CartResponseDTO addProdutoToCart(User user, Long produtoId) {
        // Validar se produto existe
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));

        // Buscar ou criar carrinho
        Cart cart = cartRepository.findCartWithProdutosByUser(user)
                .orElseGet(() -> createCartForUser(user));

        // Adicionar produto (evita duplicidade graças ao metodo da entidade)
        cart.addProduto(produto);

        // Salvar
        cart = cartRepository.save(cart);

        // Recarregar com produtos para retornar
        cart = cartRepository.findCartWithProdutosByUser(user).get();

        return convertToResponseDTO(cart);
    }

    // 4. Remover produto do carrinho
    @Transactional
    public CartResponseDTO removeProdutoFromCart(User user, Long produtoId) {
        // Buscar carrinho do usuário
        Cart cart = cartRepository.findCartWithProdutosByUser(user)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário"));

        // Buscar produto
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));

        // Remover produto
        cart.removeProduto(produto);

        // Salvar
        cart = cartRepository.save(cart);

        return convertToResponseDTO(cart);
    }

    // 5. Limpar carrinho (remover todos os produtos)
    @Transactional
    public void clearCart(User user) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário"));

        cart.clear();
        cartRepository.save(cart);
    }

    // 6. Verificar se produto está no carrinho
    public boolean isProdutoInCart(User user, Long produtoId) {
        return cartRepository.existsProdutoInCart(user, produtoId);
    }

    // 7. Contar produtos no carrinho
    public int countProdutosInCart(User user) {
        return cartRepository.countProdutosInCart(user);
    }

    // 8. Converter Cart para CartResponseDTO
    private CartResponseDTO convertToResponseDTO(Cart cart) {
        // Converter lista de produtos para CartItemDTO
        List<CartItemDTO> itens = cart.getProdutos().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        // Calcular valor total
        BigDecimal valorTotal = itens.stream()
                .map(CartItemDTO::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDTO(
                cart.getId(),
                cart.getUser().getId(),
                cart.getUser().getEmail(),
                cart.getUser().getName(),
                itens,
                itens.size(),
                valorTotal,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }

    // 9. Converter Produto para CartItemDTO
    private CartItemDTO convertToCartItemDTO(Produto produto) {
        return new CartItemDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidade(),  // ← campo quantidade do estoque
                produto.getCategoria()
        );
    }
}