package org.example.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private String cartId;
    private String userId;
    private String userEmail;
    private String userName;
    private List<CartItemDTO> itens;
    private int totalItens;
    private BigDecimal valorTotal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}