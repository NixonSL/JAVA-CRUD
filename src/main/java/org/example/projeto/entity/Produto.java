package org.example.projeto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projeto.dto.ProdutoRequestDTO;
import org.example.projeto.dto.ProdutoResponseDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 500, message = "A descrição pode ter no máximo 500 caracteres")
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "A quantidade é obrigatória")
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidade;

    @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
    @Column(length = 50)
    private String categoria;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public static Produto fromRequest(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setQuantidade(dto.getQuantidade());
        produto.setCategoria(dto.getCategoria());
        return produto;
    }

    public ProdutoResponseDTO toResponse() {
        ProdutoResponseDTO dto = new ProdutoResponseDTO();
        dto.setId(this.id);
        dto.setNome(this.nome);
        dto.setDescricao(this.descricao);
        dto.setPreco(this.preco);
        dto.setQuantidade(this.quantidade);
        dto.setCategoria(this.categoria);
        dto.setDataCriacao(this.dataCriacao);
        dto.setDataAtualizacao(this.dataAtualizacao);
        return dto;
    }
}