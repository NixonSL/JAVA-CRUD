package org.example.projeto.controller;

import jakarta.validation.Valid;
import org.example.projeto.dto.CartRequestDTO;
import org.example.projeto.dto.CartResponseDTO;
import org.example.projeto.entity.User;
import org.example.projeto.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 1. Buscar carrinho do usuário autenticado
    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        CartResponseDTO cart = cartService.getCartByUser(user);
        return ResponseEntity.ok(cart);
    }

    // 2. Adicionar produto ao carrinho
    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addProdutoToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CartRequestDTO request) {

        User user = (User) userDetails;
        CartResponseDTO updatedCart = cartService.addProdutoToCart(user, request.getProdutoId());
        return ResponseEntity.ok(updatedCart);
    }

    // 3. Remover produto do carrinho
    @DeleteMapping("/remove/{produtoId}")
    public ResponseEntity<CartResponseDTO> removeProdutoFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long produtoId) {

        User user = (User) userDetails;
        CartResponseDTO updatedCart = cartService.removeProdutoFromCart(user, produtoId);
        return ResponseEntity.ok(updatedCart);
    }

    // 4. Limpar carrinho (remover todos os produtos)
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }

    // 5. Verificar se produto está no carrinho
    @GetMapping("/check/{produtoId}")
    public ResponseEntity<Boolean> isProdutoInCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long produtoId) {

        User user = (User) userDetails;
        boolean exists = cartService.isProdutoInCart(user, produtoId);
        return ResponseEntity.ok(exists);
    }

    // 6. Contar produtos no carrinho
    @GetMapping("/count")
    public ResponseEntity<Integer> countProdutosInCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        int count = cartService.countProdutosInCart(user);
        return ResponseEntity.ok(count);
    }
}