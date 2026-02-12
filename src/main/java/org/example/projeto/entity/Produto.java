package org.example.projeto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // Indica que esta classe é uma entidade JPA mapeada para uma tabela no banco
@Table(name = "produtos") // Define o nome da tabela no banco de dados
public class Produto {

    @Id // Indica que este campo é a chave primária da tabela
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento (banco gera o ID automaticamente)
    private Long id;

    @Column(nullable = false, length = 100) // Coluna não pode ser nula, tamanho máximo 100 caracteres
    private String nome;

    @Column(columnDefinition = "TEXT") // Define como tipo TEXT no banco (suporta textos longos)
    private String descricao;

}