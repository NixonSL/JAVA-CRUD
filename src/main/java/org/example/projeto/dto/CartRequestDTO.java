package org.example.projeto.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {
    @NotNull(message = "ID do produto é obrigatório")
    @Positive(message = "ID do produto deve ser positivo")
    private Long produtoId;
}