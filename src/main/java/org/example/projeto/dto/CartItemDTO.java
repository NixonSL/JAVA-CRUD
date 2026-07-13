package org.example.projeto.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor  // ← GARANTE O CONSTRUTOR VAZIO
@AllArgsConstructor
public class CartItemDTO {
    private Long produtoId;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidade;
    private String categoria;
}