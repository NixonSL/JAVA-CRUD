package org.example.projeto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;              // NOVO
    private Integer quantidade;            // NOVO
    private String categoria;              // NOVO
    private LocalDateTime dataCriacao;     // NOVO
    private LocalDateTime dataAtualizacao; // NOVO
}