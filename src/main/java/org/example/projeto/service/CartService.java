package org.example.projeto.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.projeto.client.AuthClient;
import org.example.projeto.client.UserDTO;
import org.example.projeto.dto.CartItemDTO;
import org.example.projeto.dto.CartResponseDTO;
import org.example.projeto.entity.Cart;
import org.example.projeto.entity.Produto;
import org.example.projeto.repository.CartRepository;
import org.example.projeto.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProdutoRepository produtoRepository;
    private final AuthClient authClient;  // ← Feign Client para Auth Service

    @Transactional
    public Cart getOrCreateCart(String userId) {
        // Verificar se usuário existe no Auth Service
        log.info("Verificando existência do usuário: {}", userId);
        Boolean userExists = authClient.userExists(userId);

        if (!userExists) {
            throw new RuntimeException("Usuário não encontrado: " + userId);
        }

        // Buscar carrinho existente ou criar novo
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Criando novo carrinho para usuário: {}", userId);
                    Cart newCart = new Cart(userId);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public CartResponseDTO getCartByUserId(String userId) {
        Cart cart = getOrCreateCart(userId);
        return convertToResponseDTO(cart);
    }

    @Transactional
    public CartResponseDTO addProdutoToCart(String userId, Long produtoId) {
        // Validar se produto existe
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));

        // Buscar ou criar carrinho
        Cart cart = getOrCreateCart(userId);

        // Adicionar produto
        cart.addProduto(produto);
        cart = cartRepository.save(cart);

        // Recarregar com produtos
        cart = cartRepository.findCartWithProdutosByUserId(userId).orElse(cart);

        return convertToResponseDTO(cart);
    }

    @Transactional
    public CartResponseDTO removeProdutoFromCart(String userId, Long produtoId) {
        Cart cart = cartRepository.findCartWithProdutosByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));

        cart.removeProduto(produto);
        cart = cartRepository.save(cart);

        return convertToResponseDTO(cart);
    }

    @Transactional
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado para o usuário"));

        cart.clear();
        cartRepository.save(cart);
    }

    public boolean isProdutoInCart(String userId, Long produtoId) {
        return cartRepository.existsProdutoInCart(userId, produtoId);
    }

    public int countProdutosInCart(String userId) {
        return cartRepository.countProdutosInCart(userId);
    }

    private CartResponseDTO convertToResponseDTO(Cart cart) {
        // Buscar dados do usuário via Feign
        UserDTO userInfo = authClient.getUserById(cart.getUserId());

        List<CartItemDTO> itens = cart.getProdutos().stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());

        BigDecimal valorTotal = itens.stream()
                .map(CartItemDTO::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Sem builder - usando construtor ou setters
        CartResponseDTO response = new CartResponseDTO();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setUserEmail(userInfo.getEmail());
        response.setUserName(userInfo.getName());
        response.setItens(itens);
        response.setTotalItens(itens.size());
        response.setValorTotal(valorTotal);
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        return response;
    }

    private CartItemDTO convertToCartItemDTO(Produto produto) {
        // Sem builder - usando construtor ou setters
        CartItemDTO item = new CartItemDTO();
        item.setProdutoId(produto.getId());
        item.setNome(produto.getNome());
        item.setDescricao(produto.getDescricao());
        item.setPreco(produto.getPreco());
        item.setQuantidade(produto.getQuantidade());
        item.setCategoria(produto.getCategoria());

        return item;
    }
}