package org.example.projeto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;  // ← Agora é apenas String (ID do usuário no Auth Service)

    @ManyToMany
    @JoinTable(
            name = "cart_produtos",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    @Builder.Default
    private List<Produto> produtos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Construtor personalizado para criar carrinho com userId
    public Cart(String userId) {
        this.userId = userId;
        this.produtos = new ArrayList<>();
    }

    // Métodos utilitários
    public void addProduto(Produto produto) {
        if (!this.produtos.contains(produto)) {
            this.produtos.add(produto);
        }
    }

    public void removeProduto(Produto produto) {
        this.produtos.remove(produto);
    }

    public boolean hasProduto(Produto produto) {
        return this.produtos.contains(produto);
    }

    public void clear() {
        this.produtos.clear();
    }

    public int getTotalItems() {
        return this.produtos.size();
    }
}