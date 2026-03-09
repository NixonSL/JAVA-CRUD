package org.example.projeto.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProdutoRequestDTO {

    @NotBlank(message = "O nome do produto e obrigatorio")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
    private String nome;

    @Size(max = 500, message = "A descricao pode ter no maximo 500 caracteres")
    private String descricao;

    @NotNull(message = "O preco é obrigatorio")
    @Positive(message = "O preco deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "A quantidade e obrigatoria")
    @PositiveOrZero(message = "A quantidade nao pode ser negativa")
    private Integer quantidade;

    @Size(max = 50, message = "A categoria deve ter no maximo 50 caracteres")
    private String categoria;

    // Construtor padrao
    public ProdutoRequestDTO() {}

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}
