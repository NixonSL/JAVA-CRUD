package org.example.projeto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.projeto.dto.ProdutoRequestDTO;
import org.example.projeto.dto.ProdutoResponseDTO;

import java.math.BigDecimal; // Para preço com precisão decimal
import java.time.LocalDateTime; // Para datas de criação/atualização

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

    // NOVO CAMPO: Preço do produto
    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2) // 10 dígitos, 2 casas decimais
    private BigDecimal preco;

    // NOVO CAMPO: Quantidade em estoque
    @NotNull(message = "A quantidade é obrigatória")
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidade;

    // NOVO CAMPO: Categoria do produto
    @Size(max = 50, message = "A categoria deve ter no máximo 50 caracteres")
    @Column(length = 50)
    private String categoria;

    // NOVO CAMPO: Data de criação (automática)
    @Column(name = "data_criacao", updatable = false) // updatable=false impede alteração
    private LocalDateTime dataCriacao;

    // NOVO CAMPO: Data da última atualização (automática)
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    // Método executado automaticamente ANTES de salvar pela primeira vez
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    // Método executado automaticamente ANTES de cada atualização
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Métodos de conversão (para usar com DTOs)
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
        return new ProdutoResponseDTO(
                this.id,
                this.nome,
                this.descricao,
                this.preco,
                this.quantidade,
                this.categoria,
                this.dataCriacao,
                this.dataAtualizacao
        );
    }
}