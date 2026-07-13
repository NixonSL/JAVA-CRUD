package org.example.projeto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import static org.example.projeto.config.ApiPaths.API_V1;
import org.example.projeto.dto.CartRequestDTO;
import org.example.projeto.dto.CartResponseDTO;
import org.example.projeto.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(API_V1 + "/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // O userId agora vem do token JWT (via header)
    // Por enquanto, vamos receber como parâmetro (depois extraímos do token)

    @GetMapping("/{userId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable String userId) {
        CartResponseDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponseDTO> addProdutoToCart(
            @PathVariable String userId,
            @Valid @RequestBody CartRequestDTO request) {

        CartResponseDTO updatedCart = cartService.addProdutoToCart(userId, request.getProdutoId());
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{userId}/remove/{produtoId}")
    public ResponseEntity<CartResponseDTO> removeProdutoFromCart(
            @PathVariable String userId,
            @PathVariable Long produtoId) {

        CartResponseDTO updatedCart = cartService.removeProdutoFromCart(userId, produtoId);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/check/{produtoId}")
    public ResponseEntity<Boolean> isProdutoInCart(
            @PathVariable String userId,
            @PathVariable Long produtoId) {

        boolean exists = cartService.isProdutoInCart(userId, produtoId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{userId}/count")
    public ResponseEntity<Integer> countProdutosInCart(@PathVariable String userId) {
        int count = cartService.countProdutosInCart(userId);
        return ResponseEntity.ok(count);
    }
}
